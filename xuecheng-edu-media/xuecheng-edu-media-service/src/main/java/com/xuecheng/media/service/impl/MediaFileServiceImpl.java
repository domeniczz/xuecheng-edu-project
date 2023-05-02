package com.xuecheng.media.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.j256.simplemagic.ContentInfo;
import com.j256.simplemagic.ContentInfoUtil;
import com.xuecheng.base.exception.XueChengEduException;
import com.xuecheng.base.model.PageParams;
import com.xuecheng.base.model.PageResult;
import com.xuecheng.media.mapper.MediaFileMapper;
import com.xuecheng.media.model.dto.FileParamsDto;
import com.xuecheng.media.model.dto.FileResultDto;
import com.xuecheng.media.model.dto.QueryMediaParamsDto;
import com.xuecheng.media.model.po.MediaFile;
import com.xuecheng.media.service.MediaFileService;
import io.minio.MinioClient;
import io.minio.RemoveObjectArgs;
import io.minio.UploadObjectArgs;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.File;
import java.io.FileInputStream;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

/**
 * @author Domenic
 * @Classname MediaFileServiceImpl
 * @Description 媒资文件管理业务实现类
 * @Created by Domenic
 */
@Slf4j
@Lazy
@Service
public class MediaFileServiceImpl implements MediaFileService {

    @Autowired
    MediaFileMapper mediaFileMapper;

    @Autowired
    MinioClient minioClient;

    @Autowired
    @Lazy
    MediaFileServiceImpl currentProxy;

    /**
     * 存储普通文件的桶
     */
    @Value("${minio.bucket.files}")
    private String mediafilesBucket;

    /**
     * 存储视频的桶
     */
    @Value("${minio.bucket.videofiles}")
    private String videoBucket;

    @Override
    public PageResult<MediaFile> queryMediaFileList(Long companyId, PageParams pageParams, QueryMediaParamsDto dto) {

        LambdaQueryWrapper<MediaFile> queryWrapper = new LambdaQueryWrapper<>();

        // 匹配文件名称
        queryWrapper.eq(StringUtils.isNotEmpty(dto.getFilename()),
                MediaFile::getFilename,
                dto.getFilename());

        // 匹配文件类型
        queryWrapper.eq(StringUtils.isNotEmpty(dto.getFileType()),
                MediaFile::getFileType,
                dto.getFileType());

        // 匹配文件审核状态
        queryWrapper.eq(StringUtils.isNotEmpty(dto.getAuditStatus()),
                MediaFile::getAuditStatus,
                dto.getAuditStatus());

        // 分页对象
        Page<MediaFile> page = new Page<>(pageParams.getPageNo(), pageParams.getPageSize());
        // 查询数据内容获得结果
        Page<MediaFile> pageResult = mediaFileMapper.selectPage(page, queryWrapper);
        // 获取数据列表到 List
        List<MediaFile> list = pageResult.getRecords();

        // 获取数据总数
        long total = pageResult.getTotal();

        // 构建结果集
        return new PageResult<>(list, total, pageParams.getPageNo(), pageParams.getPageSize());
    }

    @Override
    public FileResultDto uploadMediaFile(Long companyId, FileParamsDto dto, String tempFilePath) {

        // 文件名
        String filename = dto.getFilename();

        // 文件扩展名
        String ext = filename.substring(filename.lastIndexOf("."));

        // 文件 mimeType
        String mimeType = getMimeTypeFromExt(ext);

        // 文件存储路径 (年/月/日)
        String defaultFolderPath = getDefaultFolderPath();

        // 文件 MD5 值
        String fileMd5 = getFileMd5(new File(tempFilePath));

        // 最终的文件名
        String finalFilename = filename.substring(0, filename.lastIndexOf(".")) + fileMd5 + ext;

        // 最终的文件存放路径 (路径 + 文件名)
        String objectName = defaultFolderPath + finalFilename;

        // 将上传的文件信息添加到数据库的文件信息表中
        MediaFile resDb = currentProxy.addFileToDb(companyId, fileMd5, dto, mediafilesBucket, finalFilename, objectName);

        if (resDb != null) {
            // 上传文件到 minio
            boolean resMinio = uploadFileToMinio(tempFilePath, mimeType, mediafilesBucket, objectName);

            // 准备返回的对象
            if (resMinio) {
                FileResultDto resDto = new FileResultDto();
                BeanUtils.copyProperties(resDb, resDto);
                return resDto;
            }
        }
        return null;
    }

    @Override
    public FileResultDto deleteMediaFile(long companyId, FileParamsDto dto) {

        // 从数据库中获取文件信息
        List<MediaFile> fileList = getFileInfoFromDb(companyId, dto, mediafilesBucket, null);

        MediaFile fileToDelete = null;

        // 若该文件存在且仅有一条该文件记录，则删除该信息
        if (fileList.size() == 1) {
            // 获取文件信息
            fileToDelete = fileList.get(0);

            // 删除数据库中的文件信息
            boolean res = currentProxy.deleteFileFromDbById(fileToDelete.getId());
            if (!res) {
                log.debug("从数据库删除文件信息失败, bucket:{}, objectName:{}", mediafilesBucket, fileToDelete.getFilePath());
                XueChengEduException.cast("从数据库删除文件信息失败，未知错误!");
            }

            // 将文件从 minio 中删除
            if (!deleteFileFromMinio(mediafilesBucket, fileToDelete.getFilePath())) {
                XueChengEduException.cast("删除文件失败");
            }
        } else if (fileList.size() == 0) {
            log.debug("从数据库删除文件信息失败，数据库中没有该文件的记录, bucket:{}", mediafilesBucket);
            XueChengEduException.cast("从数据库删除文件信息失败，无效文件!");
        } else {
            log.debug("从数据库删除文件信息失败，数据库中有 {} 条该文件 (id = {}) 的记录, bucket:{}",
                    fileList.size(), fileList.get(0).getId(), mediafilesBucket);
            XueChengEduException.cast("从数据库删除文件信息失败，未知错误!");
        }

        // 准备返回的对象
        FileResultDto resDto = new FileResultDto();
        BeanUtils.copyProperties(fileToDelete, resDto);

        return resDto;
    }

    /**
     * 将 媒体/视频 文件上传到 minio
     * @param localFilePath 文件本地路径
     * @param mimeType 媒体类型
     * @param bucket 桶
     * @param objectName 对象名
     * @return 是否上传成功
     */
    private boolean uploadFileToMinio(String localFilePath, String mimeType, String bucket, String objectName) {
        try {
            UploadObjectArgs args = UploadObjectArgs.builder()
                    // 桶
                    .bucket(bucket)
                    // 指定本地文件路径
                    .filename(localFilePath)
                    // 对象名 放在子目录下
                    .object(objectName)
                    // 设置媒体文件类型
                    .contentType(mimeType)
                    .build();

            // 上传文件
            minioClient.uploadObject(args);

            log.debug("上传文件到 minio 成功, bucket:{}, objectName:{}", bucket, objectName);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            log.error("上传文件到 minio 出错, bucket:{}, objectName:{}, error message:{}", bucket, objectName, e.getMessage());
            XueChengEduException.cast("上传文件失败");
            return false;
        }
    }

    /**
     * 将 媒体/视频 文件从 minio 中删除
     * @param bucket 桶名称
     * @param objectName 对象名 (存储路径)
     * @return 是否删除成功
     */
    private boolean deleteFileFromMinio(String bucket, String objectName) {
        // 删除文件
        try {
            RemoveObjectArgs args = RemoveObjectArgs.builder()
                    .bucket(mediafilesBucket)
                    .object(objectName)
                    .build();

            minioClient.removeObject(args);

            log.debug("从 minio 删除文件成功, bucket:{}, objectName:{}", bucket, objectName);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            log.error("从 minio 删除文件出错, bucket:{}, objectName:{}, error message:{}", bucket, objectName, e.getMessage());
            XueChengEduException.cast("删除文件失败");
            return false;
        }
    }

    /**
     * 将 媒体/视频 文件信息添加到文件信息表中
     * @param companyId 机构 ID
     * @param fileMd5 文件 MD5 值
     * @param dto 文件操作 (上传) 参数 DTO
     * @param bucket 存储桶名
     * @param filename 文件名
     * @param objectName 对象名 (存储路径)
     * @return MediaFile 对象
     */
    @Transactional(rollbackFor = Exception.class)
    public MediaFile addFileToDb(Long companyId, String fileMd5, FileParamsDto dto, String bucket, String filename, String objectName) {
        // 查询数据库中是否已存在该文件
        MediaFile mediaFile = mediaFileMapper.selectOne(new LambdaQueryWrapper<MediaFile>().eq(MediaFile::getId, fileMd5));

        // 数据库中不存在该文件, 将文件信息入库
        if (mediaFile == null) {

            mediaFile = new MediaFile();
            BeanUtils.copyProperties(dto, mediaFile);

            // 文件 ID
            mediaFile.setId(fileMd5);
            // 机构 ID
            mediaFile.setCompanyId(companyId);
            // 机构名称
            // mediaFile.setCompanyName("");
            // 文件名
            mediaFile.setFilename(filename);
            // 标签
            // mediaFile.setTags("");
            // 备注
            // mediaFile.setRemark("");
            // 桶
            mediaFile.setBucket(bucket);
            // 文件存储路径
            mediaFile.setFilePath(objectName);
            // 文件标识 ID
            mediaFile.setFileId(fileMd5);
            // 文件访问 url
            mediaFile.setUrl("/" + bucket + "/" + objectName);
            // 上传人
            // mediaFile.setUsername("");
            // 上传时间
            mediaFile.setCreateDate(LocalDateTime.now());
            // 状态
            mediaFile.setStatus("1");
            // 审核状态：审核通过
            mediaFile.setAuditStatus("002003");

            // 插入数据库
            int res = mediaFileMapper.insert(mediaFile);

            if (res <= 0) {
                log.debug("向数据库保存文件失败, bucket:{}, objectName:{}", bucket, objectName);
                XueChengEduException.cast("文件上传后保存信息失败");
                return null;
            }
        }
        return mediaFile;
    }

    /**
     * <p>
     * 从文件信息表中删除 媒体/视频 文件<br/>
     * 注意：删除数据库中对应的文件信息时，不是根据文件 ID (MD5 值) 删除，而是根据文件其他元信息删除
     * </p>
     * @param fileId 文件 ID (Hash 值)
     * @return 是否删除成功
     */
    @Transactional(rollbackFor = Exception.class)
    public boolean deleteFileFromDbById(String fileId) {

        int res = mediaFileMapper.deleteById(fileId);

        if (res <= 0) {
            log.debug("从数据库删除文件 (id = {}) 信息失败", fileId);
            XueChengEduException.cast("从数据库删除文件信息失败，未知错误!");
            return false;
        }

        return true;
    }

    /**
     * 从文件信息表中查询 媒体/视频 文件
     * @param companyId 机构 ID
     * @param dto 文件操作 (查询) 参数 DTO
     * @param bucket 存储桶名
     * @param objectName 对象名 (存储路径)
     * @return 媒体/视频 文件信息的 List
     */
    private List<MediaFile> getFileInfoFromDb(long companyId, FileParamsDto dto, String bucket, String objectName) {
        return mediaFileMapper.selectList(new LambdaQueryWrapper<MediaFile>()
                // 机构 ID
                .eq(MediaFile::getCompanyId, companyId)
                // 文件名
                .eq(StringUtils.isNotEmpty(dto.getFilename()), MediaFile::getFilename, dto.getFilename())
                // 文件类型
                .eq(StringUtils.isNotEmpty(dto.getFileType()), MediaFile::getFileType, dto.getFileType())
                // 文件存储路径
                .eq(objectName != null, MediaFile::getFilePath, objectName)
                // 文件访问 url
                .eq(objectName != null, MediaFile::getUrl, "/" + bucket + "/" + objectName)
                // 文件大小
                .eq(dto.getFileSize() >= 0, MediaFile::getFileSize, dto.getFileSize())
                // 文件标签
                .eq(StringUtils.isNotEmpty(dto.getTags()), MediaFile::getTags, dto.getTags())
                // 文件上传人名称
                .eq(StringUtils.isNotEmpty(dto.getUsername()), MediaFile::getUsername, dto.getUsername())
                // 文件备注
                .eq(StringUtils.isNotEmpty(dto.getRemark()), MediaFile::getRemark, dto.getRemark()));
    }

    /**
     * 获取文件默认存储路径，示例：年/月/日
     * @return 路径 (String)
     */
    private String getDefaultFolderPath() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        return sdf.format(new Date()).replace("-", "/") + "/";
    }

    /**
     * 根据扩展名获取文件的 mimeType
     * @param ext 文件扩展名
     * @return mimeType (String)
     */
    private String getMimeTypeFromExt(String ext) {

        if (ext == null) {
            ext = "";
        }

        // 根据扩展名获取 mimeType
        ContentInfo extMatch = ContentInfoUtil.findExtensionMatch(ext);
        // 通用 mimeType，字节流
        String mimeType = MediaType.APPLICATION_OCTET_STREAM_VALUE;
        if (extMatch != null) {
            mimeType = extMatch.getMimeType();
        }

        return mimeType;
    }

    /**
     * 获取文件的 MD5
     * @param file 文件
     * @return MD5 (String)
     */
    private String getFileMd5(File file) {
        try (FileInputStream fis = new FileInputStream(file)) {
            return DigestUtils.md5Hex(fis);
        } catch (Exception e) {
            log.error("获取文件 \"" + file.getName() + "\" MD5 失败!", e);
            return null;
        }
    }

}