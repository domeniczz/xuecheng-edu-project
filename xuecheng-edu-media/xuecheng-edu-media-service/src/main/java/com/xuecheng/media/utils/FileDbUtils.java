package com.xuecheng.media.utils;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.xuecheng.base.exception.XueChengEduException;
import com.xuecheng.media.mapper.MediaFileMapper;
import com.xuecheng.media.model.dto.FileParamsDto;
import com.xuecheng.media.model.po.MediaFile;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

import lombok.extern.slf4j.Slf4j;

/**
 * @author Domenic
 * @Classname FileDbUtils
 * @Description 文件信息数据库操作
 * @Created by Domenic
 */
@Component
@Slf4j
public class FileDbUtils {

    @Autowired
    private MediaFileMapper mediaFileMapper;

    /**
     * 将 媒体/视频 文件信息添加到文件信息表中
     * @param companyId 机构 ID
     * @param fileMd5 文件 MD5 值
     * @param dto 文件操作 (上传) 参数 DTO
     * @param bucket 存储桶名
     * @param filename 文件名
     * @param objectName 对象名 (文件的路径)
     * @return 添加的 {@link MediaFile} 对象
     */
    @Transactional(rollbackFor = Exception.class)
    public MediaFile addFileInfo(Long companyId, String fileMd5, FileParamsDto dto, String bucket, String filename, String objectName) {
        // 查询数据库中是否已存在该文件
        MediaFile mediaFile = getOneFileInfo(fileMd5);

        // 若数据库中不存在该文件, 将文件信息入库
        if (mediaFile == null) {
            mediaFile = saveFileInfo(companyId, fileMd5, dto, bucket, filename, objectName);
        }

        return mediaFile;
    }

    /**
     * 更新文件信息表中的 媒体/视频 文件信息
     * @param companyId 机构 ID
     * @param fileMd5 文件 MD5 值
     * @param dto 文件操作 (上传) 参数 DTO
     * @param bucket 存储桶名
     * @param filename 文件名
     * @param objectName 对象名 (文件的路径)
     * @return 更新后的 {@link MediaFile} 对象
     */
    @Transactional(rollbackFor = Exception.class)
    public MediaFile updateFileInfo(Long companyId, String fileMd5, FileParamsDto dto, String bucket, String filename, String objectName) {
        // 查询数据库中是否存在该文件
        MediaFile mediaFile = getOneFileInfo(fileMd5);

        // 若数据库中存在该文件, 将文件信息入库
        if (mediaFile != null) {
            return saveFileInfo(companyId, fileMd5, dto, bucket, filename, objectName);
        } else {
            log.error("尝试更新数据库中不存在的文件信息, objectName={}, fileMd5={}", objectName, fileMd5);
        }

        return null;
    }

    /**
     * 从文件信息表中删除 媒体/视频 文件的信息
     * @param file 要删除信息的文件
     */
    @Transactional(rollbackFor = Exception.class)
    public boolean deleteFileInfo(MediaFile file) {
        int res = mediaFileMapper.delete(new LambdaQueryWrapper<MediaFile>()
                // 机构 ID
                .eq(file.getCompanyId() != null, MediaFile::getCompanyId, file.getCompanyId())
                // 文件名
                .eq(StringUtils.isNotEmpty(file.getFilename()), MediaFile::getFilename, file.getFilename())
                // 文件类型
                .eq(StringUtils.isNotEmpty(file.getFileType()), MediaFile::getFileType, file.getFileType())
                // 文件存储路径
                .eq(StringUtils.isNotEmpty(file.getFilePath()), MediaFile::getFilePath, file.getFilePath())
                // 文件所在桶名
                .eq(StringUtils.isNotEmpty(file.getBucket()), MediaFile::getBucket, file.getBucket())
                // 文件大小
                .eq(file.getFileSize() != null, MediaFile::getFileSize, file.getFileSize())
                // 文件标签
                .eq(StringUtils.isNotEmpty(file.getTags()), MediaFile::getTags, file.getTags())
                // 文件上传人名称
                .eq(StringUtils.isNotEmpty(file.getUsername()), MediaFile::getUsername, file.getUsername())
                // 文件备注
                .eq(StringUtils.isNotEmpty(file.getRemark()), MediaFile::getRemark, file.getRemark()));

        if (res > 0) {
            log.debug("媒资文件删除成功, objectName={}", file.getFilePath());
            return true;
        } else {
            log.error("从数据库删除文件信息失败, objectName={}", file.getFilePath());
            XueChengEduException.cast("从数据库删除文件信息失败，未知错误!");
            return false;
        }
    }

    /**
     * 不提供文件 MD5 值，从文件信息表中查询 媒体/视频 文件列表
     * @param companyId 机构 ID
     * @param dto 文件操作 (查询) 参数 DTO
     * @param bucket 存储桶名
     * @param objectName 对象名 (文件的路径)
     * @return 媒体/视频 文件信息的 {@link List}
     */
    public List<MediaFile> getListFileInfo(Long companyId, FileParamsDto dto, String bucket, String objectName) {
        return mediaFileMapper.selectList(new LambdaQueryWrapper<MediaFile>()
                // 机构 ID
                .eq(companyId != null, MediaFile::getCompanyId, companyId)
                // 文件名
                .eq(StringUtils.isNotEmpty(dto.getFilename()), MediaFile::getFilename, dto.getFilename())
                // 文件类型
                .eq(StringUtils.isNotEmpty(dto.getFileType()), MediaFile::getFileType, dto.getFileType())
                // 文件存储路径
                .eq(objectName != null, MediaFile::getFilePath, objectName)
                // 文件访问 url
                .eq(objectName != null, MediaFile::getUrl, "/" + bucket + "/" + objectName)
                // 文件大小
                .eq(dto.getFileSize() != null, MediaFile::getFileSize, dto.getFileSize())
                // 文件标签
                .eq(StringUtils.isNotEmpty(dto.getTags()), MediaFile::getTags, dto.getTags())
                // 文件上传人名称
                .eq(StringUtils.isNotEmpty(dto.getUsername()), MediaFile::getUsername, dto.getUsername())
                // 文件备注
                .eq(StringUtils.isNotEmpty(dto.getRemark()), MediaFile::getRemark, dto.getRemark()));
    }

    /**
     * 提供文件 MD5，从数据库中查询该文件的信息
     * @param fileMd5 文件 MD5 值
     * @return 文件信息对象 {@link MediaFile}，若不存在则返回 null
     */
    public MediaFile getOneFileInfo(String fileMd5) {
        return mediaFileMapper.selectOne(new LambdaQueryWrapper<MediaFile>().eq(MediaFile::getId, fileMd5));
    }

    private MediaFile saveFileInfo(Long companyId, String fileMd5, FileParamsDto dto, String bucket, String filename, String objectName) {
        MediaFile mediaFile = new MediaFile();
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
            log.error("文件信息保存到数据表 media_file 失败, bucket={}, objectName={}", bucket, objectName);
            XueChengEduException.cast("文件上传后保存信息失败");
            return null;
        }
        log.debug("文件信息保存到数据表 media_file 成功, fileMd5={}, objectName={}", fileMd5, objectName);
        return mediaFile;
    }

}
