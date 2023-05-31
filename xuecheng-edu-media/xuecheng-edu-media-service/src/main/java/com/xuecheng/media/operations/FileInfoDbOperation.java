package com.xuecheng.media.operations;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.xuecheng.base.exception.XueChengEduException;
import com.xuecheng.media.mapper.MediaFileMapper;
import com.xuecheng.media.model.po.MediaFile;
import com.xuecheng.media.service.MediaProcessService;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

import lombok.extern.slf4j.Slf4j;

/**
 * @author Domenic
 * @Classname FileInfoDbOperation
 * @Description 文件信息数据库操作的工具类
 * @Created by Domenic
 */
@Component
@Slf4j
public class FileInfoDbOperation {

    @Autowired
    private MediaFileMapper mediaFileMapper;

    @Autowired
    private MediaProcessService mediaProcessService;

    @Autowired
    private FileInfoDbOperation currentProxy;

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
    public MediaFile addFileInfo(MediaFile mediaFileToAdd) {
        if (StringUtils.isBlank(mediaFileToAdd.getId())) {
            log.error("添加文件信息时, 传入的 MD5 值为空, objectName={}", mediaFileToAdd.getFilePath());
            throw new IllegalArgumentException("添加文件信息时, 传入的 MD5 值为空");
        }

        // 查询数据库中是否已存在该文件
        MediaFile mediaFile = currentProxy.getOneFileInfo(mediaFileToAdd.getId());

        // 若数据库中不存在该文件
        if (mediaFile == null) {
            // 将文件信息入库
            mediaFile = saveFileInfo(mediaFileToAdd);
            // 将文件添加到待处理文件列表中
            mediaProcessService.addPendingTask(mediaFile);
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
    public MediaFile updateFileInfo(MediaFile mediaFileToUpdate) {
        if (StringUtils.isBlank(mediaFileToUpdate.getId())) {
            log.error("更新文件信息时, 传入的 MD5 值为空, objectName={}", mediaFileToUpdate.getFilePath());
            throw new IllegalArgumentException("更新文件信息时, 传入的 MD5 值为空");
        }

        // 查询数据库中是否存在该文件
        MediaFile mediaFile = currentProxy.getOneFileInfo(mediaFileToUpdate.getId());

        // 若数据库中存在该文件, 将文件信息入库
        if (mediaFile != null) {
            return saveFileInfo(mediaFileToUpdate);
        } else {
            log.error("尝试更新数据库中不存在的文件信息, objectName={}, fileMd5={}", mediaFileToUpdate.getFilePath(), mediaFileToUpdate.getId());
            XueChengEduException.cast("尝试更新数据库中不存在的文件信息");
            return null;
        }
    }

    /**
     * 从文件信息表中删除 媒体/视频 文件的信息
     * @param file 要删除信息的文件
     */
    @Transactional(rollbackFor = Exception.class)
    public boolean deleteFileInfo(MediaFile file) {
        if (file != null) {
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
            }
        } else {
            log.error("删除文件信息失败, 传入的文件信息为空");
            throw new IllegalArgumentException("传入的文件信息为空");
        }
        return false;
    }

    /**
     * 不提供文件 MD5 值，从文件信息表中查询 媒体/视频 文件列表
     * @param companyId 机构 ID
     * @param dto 文件操作 (查询) 参数 DTO
     * @param bucket 存储桶名
     * @param objectName 对象名 (文件的路径)
     * @return 媒体/视频 文件信息的 {@link List}
     */
    @Transactional(propagation = Propagation.NOT_SUPPORTED)
    public List<MediaFile> getListFileInfo(MediaFile fileToGet) {
        if (fileToGet != null) {
            return mediaFileMapper.selectList(new LambdaQueryWrapper<MediaFile>()
                    // 机构 ID
                    .eq(fileToGet.getCompanyId() != null, MediaFile::getCompanyId, fileToGet.getCompanyId())
                    // 文件名
                    .eq(StringUtils.isNotEmpty(fileToGet.getFilename()), MediaFile::getFilename, fileToGet.getFilename())
                    // 文件类型
                    .eq(StringUtils.isNotEmpty(fileToGet.getFileType()), MediaFile::getFileType, fileToGet.getFileType())
                    // 文件存储路径
                    .eq(fileToGet.getFilePath() != null, MediaFile::getFilePath, fileToGet.getFilePath())
                    // 文件访问 url
                    .eq(fileToGet.getFilePath() != null, MediaFile::getUrl, "/" + fileToGet.getBucket() + "/" + fileToGet.getFilePath())
                    // 文件大小
                    .eq(fileToGet.getFileSize() != null, MediaFile::getFileSize, fileToGet.getFileSize())
                    // 文件标签
                    .eq(StringUtils.isNotEmpty(fileToGet.getTags()), MediaFile::getTags, fileToGet.getTags())
                    // 文件上传人名称
                    .eq(StringUtils.isNotEmpty(fileToGet.getUsername()), MediaFile::getUsername, fileToGet.getUsername())
                    // 文件备注
                    .eq(StringUtils.isNotEmpty(fileToGet.getRemark()), MediaFile::getRemark, fileToGet.getRemark()));
        } else {
            log.error("查询文件列表失败, 传入的 FileParamsDto 为空");
            throw new IllegalArgumentException("传入的 FileParamsDto 为空");
        }
    }

    /**
     * 提供文件 MD5，从数据库中查询该文件的信息
     * @param fileMd5 文件 MD5 值
     * @return 文件信息对象 {@link MediaFile}，若不存在则返回 null
     */
    @Transactional(propagation = Propagation.NOT_SUPPORTED)
    public MediaFile getOneFileInfo(String fileMd5) {
        if (StringUtils.isBlank(fileMd5)) {
            log.error("获取文件信息失败, 传入的 MD5 值为空");
            throw new IllegalArgumentException("获取文件信息失败, 传入的 MD5 值为空");
        }

        return mediaFileMapper.selectOne(new LambdaQueryWrapper<MediaFile>().eq(MediaFile::getId, fileMd5));
    }

    /**
     * 保存文件信息到数据库
     * @param companyId 机构 ID
     * @param fileMd5 文件 MD5 值
     * @param dto 文件操作 (保存) 参数 DTO
     * @param bucket 存储桶名
     * @param filename 文件名
     * @param objectName 对象名 (文件的路径)
     * @return
     */
    private MediaFile saveFileInfo(MediaFile mediaFileToSave) {
        // 文件标识 ID
        mediaFileToSave.setFileId(mediaFileToSave.getId());
        // 文件访问 url
        mediaFileToSave.setUrl("/" + mediaFileToSave.getBucket() + "/" + mediaFileToSave.getFilePath());
        // 上传时间
        mediaFileToSave.setCreateDate(LocalDateTime.now());
        // 状态
        mediaFileToSave.setStatus("1");
        // 审核状态：审核通过
        mediaFileToSave.setAuditStatus("002003");

        // 插入数据库
        int res = mediaFileMapper.insert(mediaFileToSave);

        if (res <= 0) {
            log.error("文件信息保存到数据表 media_file 失败, bucket={}, objectName={}", mediaFileToSave.getBucket(), mediaFileToSave.getFilePath());
            XueChengEduException.cast("文件上传后保存信息失败");
            return null;
        }
        log.debug("文件信息保存到数据表 media_file 成功, fileMd5={}, objectName={}", mediaFileToSave.getId(), mediaFileToSave.getFilePath());

        return mediaFileToSave;
    }

}
