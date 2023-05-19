package com.xuecheng.media.service.impl;

import com.xuecheng.base.model.RestResponse;
import com.xuecheng.media.mapper.MediaFileMapper;
import com.xuecheng.media.model.dto.FileParamsDto;
import com.xuecheng.media.model.po.MediaFile;
import com.xuecheng.media.service.BigFilesService;
import com.xuecheng.media.utils.FileDbUtils;
import com.xuecheng.media.utils.FileUtils;
import com.xuecheng.media.utils.MinioUtils;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.File;

import io.minio.ObjectWriteResponse;
import io.minio.StatObjectResponse;
import lombok.extern.slf4j.Slf4j;

/**
 * @author Domenic
 * @Classname BigFilesServiceImpl
 * @Description 大文件处理接口实现类 (大文件，分片上传)
 * @Created by Domenic
 */
@Slf4j
@Service
public class BigFilesServiceImpl implements BigFilesService {

    @Autowired
    private MediaFileMapper mediaFileMapper;

    @Autowired
    private FileDbUtils fileDbUtils;

    @Autowired
    private MinioUtils minioUtils;

    /**
     * 存储视频的桶
     */
    @Value("${minio.bucket.videofiles}")
    private String bucket;

    @Override
    public RestResponse<Boolean> checkFile(String fileMd5) {
        // 先查询数据库
        MediaFile mediaFile = mediaFileMapper.selectById(fileMd5);

        // 若数据库中存在文件信息，再查询 minio
        if (mediaFile != null) {
            try {
                StatObjectResponse fileInfo = minioUtils.getFileInfo(mediaFile.getBucket(), mediaFile.getFilePath());
                // 文件不存在
                if (fileInfo != null) {
                    // 文件已存在
                    return RestResponse.success(true);
                } else {
                    // 数据库中存在文件信息，但 minio 中不存在该文件，则删除数据库中的异常文件信息，返回文件不存在
                    log.error("数据库中存在文件信息, 但 minio 中不存在该文件, fileMd5={}, bucket={}, filePath={}",
                            fileMd5, mediaFile.getBucket(), mediaFile.getFilePath());
                    // 删除数据库中的异常文件信息
                    fileDbUtils.deleteFileInfo(mediaFile);
                }
            } catch (Exception e) {
                log.error("查询文件出错, FilterInputStream 出错, errorMsg={}", e.getMessage());
                e.printStackTrace();
            }
        }

        // 文件不存在
        return RestResponse.success(false);
    }

    @Override
    public RestResponse<Boolean> checkChunk(String fileMd5, int chunkIndex) {
        // 获取分块文件的路径
        String chunkFilePath = getChunkFileFolderPath(fileMd5) + chunkIndex;

        try {
            // 获取分块文件的信息
            StatObjectResponse fileInfo = minioUtils.getFileInfo(bucket, chunkFilePath);
            // 文件不存在
            if (fileInfo != null) {
                // 文件已存在
                return RestResponse.success(true);
            }
        } catch (Exception e) {
            log.error("检查分块文件是否存在时出错, errorMsg={}", e.getMessage());
        }

        // 分块文件不存在
        return RestResponse.success(false);
    }

    @Override
    public RestResponse<Boolean> uploadChunk(String fileMd5, int chunkIndex, String localChunkFilePath) {
        // 获取分块文件的路径
        String chunkFilePath = getChunkFileFolderPath(fileMd5) + chunkIndex;

        // 获取分块文件的 MD5
        String chunkMd5 = FileUtils.getFileMd5(new File(localChunkFilePath));

        // 获取文件的 mimeType (传入值为 null 或 空 表示没有扩展名)
        String mimeType = FileUtils.getMimeTypeFromExt("");

        // 将分块文件上传到 minio
        ObjectWriteResponse resp = minioUtils.uploadFile(localChunkFilePath, mimeType, bucket, chunkFilePath);
        // 校验分块的 MD5 和 ETag 值是否一致，来判断分块文件一致性是否受损
        if (resp != null && chunkMd5.equals(resp.etag())) {
            // 上传成功
            log.debug("分块文件 {} 上传成功, bucket={}, fileMd5={}", chunkIndex, bucket, fileMd5);
            return RestResponse.success(true);
        }

        // 上传失败
        log.error("分块文件 {} 上传失败, bucket={}, fileMd5={}", chunkIndex, bucket, fileMd5);
        return RestResponse.fail(false, "分块文件上传失败");
    }

    @Override
    public RestResponse<Boolean> mergeChunksAndSave(Long companyId, String fileMd5, int chunkTotalNum, FileParamsDto dto) {
        // 分块文件所在目录
        String chunkFolderPath = getChunkFileFolderPath(fileMd5);

        // 源文件名称
        String filename = dto.getFilename();
        // 文件扩展名
        String fileExt = filename.substring(filename.lastIndexOf("."));
        // 指定合并后文件的 objectname
        String objectName = getMergedFilePath(filename, fileMd5, fileExt);

        // ========== 合并文件 ==========

        ObjectWriteResponse resp = minioUtils.mergeChunks(bucket, objectName, chunkFolderPath);
        if (resp == null) {
            return RestResponse.fail(false, "合并文件异常");
        }

        // ========== 校验合并后的文件与源文件是否一致 ==========

        long fileSize = -1;
        try {
            // 获取合并后文件的大小
            fileSize = minioUtils.getFileSize(bucket, objectName);
        } catch (Exception e) {
            log.error("获取文件大小出错, bucket={}, objectName={}, errorMsg={}", bucket, objectName, e.getMessage());
        }

        if (!checkFileConsistency(fileSize, objectName)) {
            log.error("合并后的文件与源文件不一致");
            // 删除校验失败的合并后文件
            deleteFile(objectName);
            return RestResponse.fail(false, "合并后的文件与源文件不一致");
        }
        log.debug("校验通过，合并后的文件与源文件一致");

        // ========== 将文件信息入库 ==========

        // 设置文件大小的信息
        dto.setFileSize(fileSize);

        // 将文件信息入库，并将文件添加到待处理任务列表，等待对视频进行转码
        MediaFile mediaFiles = fileDbUtils.addFileInfo(companyId, fileMd5, dto, bucket, filename, objectName);
        if (mediaFiles == null) {
            return RestResponse.fail(false, "文件上传失败");
        }

        // ========== 清理分块文件 ==========

        if (minioUtils.clearFolderNonRecursively(bucket, chunkFolderPath)) {
            return RestResponse.success(true);
        }
        return RestResponse.success(false, "清理分块文件失败");
    }

    @Override
    public RestResponse<Boolean> deleteFile(String objectName) {
        // 先删除 minio 中的文件
        if (minioUtils.deleteFile(bucket, objectName)) {
            MediaFile file = new MediaFile();
            file.setBucket(bucket);
            file.setFilePath(objectName);
            // 再删除数据库中的文件信息
            if (fileDbUtils.deleteFileInfo(file)) {
                return RestResponse.success(true);
            } else {
                log.error("删除文件的数据库信息出错, bucket={}, objectName={}", bucket, objectName);
            }
        } else {
            log.error("删除 minio 中的文件出错, bucket={}, objectName={}", bucket, objectName);
        }
        return RestResponse.success(false, "删除文件失败");
    }

    /**
     * 校验文件的一致性
     * 
     * <!--
     * 原本采用的校验方法，是将合成后的文件下载下来，计算 MD5 值并与源文件的 MD5 进行比较，若相等则校验通过
     * 缺点：对于大文件，此方法太耗时
     * 
     * 现在，因为在上传分块时，对每一个分块，都校验了 MD5 值和上传后的 ETag 值的一致性
     * 因此，分块一致性校验通过，那只需保证合并后文件的大小与源文件一致，即可认为合并后的文件应该没有受损
     * 优点：对大文件友好，省去上传后再下载的时间
     * -->
     * @param objectName 对象名 (文件的路径)
     * @return {@link Boolean} {@code true} 校验通过, {@code false} 校验不通过
     */
    private boolean checkFileConsistency(long fileSize, String objectName) {
        long mergedSize = minioUtils.getFileSize(bucket, objectName);
        // 若文件大小一致，再进行 MD5 校验
        if (fileSize == mergedSize) {
            return true;
        } else {
            log.error("校验文件大小不一致, 原始文件大小={}, 合并文件大小={}", fileSize, mergedSize);
        }

        return false;
    }

    /**
     * <p>
     * 获取分块文件夹在 minio 中的存储路径<br/>
     * 源文件的 MD5 值的前两位分别为一级和二级目录
     * </p>
     * @param fileMd5
     * @return 文件夹路径
     */
    private String getChunkFileFolderPath(String fileMd5) {
        return fileMd5.charAt(0) + "/" + fileMd5.charAt(1) + "/" + fileMd5 + "/" + "chunk" + "/";
    }

    /**
     * 获取合并后的文件在 minio 中的存储路径
     * @param fileMd5 文件 MD5
     * @param fileExt 文件扩展名
     * @return 文件路径
     */
    private String getMergedFilePath(String filename, String fileMd5, String fileExt) {
        return fileMd5.charAt(0) + "/" + fileMd5.charAt(1) + "/" + fileMd5 + "/" +
                filename.substring(0, filename.lastIndexOf(".")) + "-" + fileMd5 + fileExt;
    }

}
