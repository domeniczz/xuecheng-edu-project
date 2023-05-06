package com.xuecheng.media.service.impl;

import com.xuecheng.base.exception.XueChengEduException;
import com.xuecheng.base.model.RestResponse;
import com.xuecheng.media.mapper.MediaFileMapper;
import com.xuecheng.media.model.dto.FileParamsDto;
import com.xuecheng.media.model.po.MediaFile;
import com.xuecheng.media.service.BigFilesService;
import com.xuecheng.media.utils.FileDbUtils;
import com.xuecheng.media.utils.FileUtils;
import com.xuecheng.media.utils.MinioUtils;

import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileInputStream;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import io.minio.ComposeSource;
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
                    // 数据库中存在文件信息，但 minio 中不存在该文件，抛出异常，并将数据库中的异常文件信息删除
                    log.error("数据库中存在文件信息, 但 minio 中不存在该文件, fileMd5={}, bucket={}, filePath={}",
                            fileMd5, mediaFile.getBucket(), mediaFile.getFilePath());
                    // 删除数据库中的异常文件信息
                    fileDbUtils.deleteFileInfo(mediaFile);
                    XueChengEduException.cast("上传文件失败，请稍后重试");
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

        // 获取文件的 mimeType (传入值为 null 或 空 表示没有扩展名)
        String mimeType = FileUtils.getMimeTypeFromExt("");

        // 将分块文件上传到 minio
        if (minioUtils.uploadFile(localChunkFilePath, mimeType, bucket, chunkFilePath) != null) {
            // 上传成功
            log.debug("分块文件 {} 上传成功, bucket={}, fileMd5={}", chunkIndex, bucket, fileMd5);
            return RestResponse.success(true);
        }
        // 上传失败
        log.error("分块文件 {} 上传失败, bucket={}, fileMd5={}, errorMsg={}", chunkIndex, bucket, fileMd5);
        return RestResponse.fail(false, "分块文件上传失败");
    }

    @Override
    public RestResponse<Boolean> mergeChunksAndSave(Long companyId, String fileMd5, int chunkTotalNum, FileParamsDto dto) {
        // 分块文件所在目录
        String chunkFolderPath = getChunkFileFolderPath(fileMd5);

        // 列出所有的分块文件
        List<ComposeSource> sources = Stream.iterate(0, i -> ++i).limit(chunkTotalNum)
                .map(i -> ComposeSource.builder()
                        .bucket(bucket)
                        .object(chunkFolderPath + i)
                        .build())
                .collect(Collectors.toList());

        // 源文件名称
        String filename = dto.getFilename();
        // 文件扩展名
        String fileExt = filename.substring(filename.lastIndexOf("."));
        // 指定合并后文件的 objectname
        String objectName = getMergedFilePath(filename, fileMd5, fileExt);

        // ========== 合并文件 ==========

        ObjectWriteResponse resp = minioUtils.mergeChunks(bucket, objectName, sources);
        if (resp == null) {
            return RestResponse.fail(false, "合并文件异常");
        }

        // ========== 校验合并后的文件与源文件是否一致 ==========

        try {
            if (!checkFileConsistencyByDownload(fileMd5, objectName)) {
                log.error("合并后的文件与源文件不一致");
                return RestResponse.fail(false, "合并后的文件与源文件不一致");
            }
            log.debug("校验通过，合并后的文件与源文件一致");
        } catch (Exception e) {
            log.error("文件上传后, 校验出错, errorMsg={}", e.getMessage());
            return RestResponse.fail(false, "文件上传后，校验出错");
        }

        // ========== 将文件信息入库 ==========

        // 设置文件大小的信息
        try {
            dto.setFileSize(minioUtils.getFileSize(bucket, objectName));
        } catch (Exception e) {
            log.error("获取文件大小出错, bucket={}, objectName={}, errorMsg={}", bucket, objectName, e.getMessage());
        }
        // 将文件信息入库
        MediaFile mediaFiles = fileDbUtils.addFileInfo(companyId, fileMd5, dto, bucket, filename, objectName);
        if (mediaFiles == null) {
            return RestResponse.fail(false, "文件上传失败");
        }

        // ========== 清理分块文件 ==========

        if (minioUtils.clearChunkFiles(bucket, chunkFolderPath, chunkTotalNum)) {
            return RestResponse.success(true);
        }
        return RestResponse.success(false, "清理分块文件失败");
    }

    @Override
    public RestResponse<Boolean> deleteFile(String objectName) {
        if (minioUtils.deleteFile(bucket, objectName)) {
            return RestResponse.success(true);
        }
        return RestResponse.success(false, "删除文件失败");
    }

    /**
     * <p>
     * 将 Minio 中的文件下载下来，计算 MD5 值，再和本地文件的 MD5 值进行比较<br/>
     * 缺点：需要下载文件，对于大文件会比较耗时
     * </p>
     * @param fileMd5
     * @param objectName
     * @return
     * @throws Exception
     */
    private boolean checkFileConsistencyByDownload(String fileMd5, String objectName) throws Exception {
        File mergedFile = minioUtils.downloadFile(bucket, objectName);
        try (FileInputStream fis = new FileInputStream(mergedFile)) {
            // 计算合并后文件的 MD5
            String mergeFileMd5 = DigestUtils.md5Hex(fis);
            // 比较原始和合并后文件的 MD5
            if (fileMd5.equals(mergeFileMd5)) {
                return true;
            } else {
                log.error("校验合并文件 MD5 值不一致, 原始文件={}, 合并文件={}", fileMd5, mergeFileMd5);
            }
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
