package com.xuecheng.media.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xuecheng.base.exception.XueChengEduException;
import com.xuecheng.base.model.PageParams;
import com.xuecheng.base.model.PageResult;
import com.xuecheng.media.mapper.MediaFileMapper;
import com.xuecheng.media.model.dto.FileParamsDto;
import com.xuecheng.media.model.dto.FileResultDto;
import com.xuecheng.media.model.dto.QueryMediaParamsDto;
import com.xuecheng.media.model.po.MediaFile;
import com.xuecheng.media.service.MediaFileService;
import com.xuecheng.media.utils.FileDbUtils;
import com.xuecheng.media.utils.FileUtils;
import com.xuecheng.media.utils.MinioUtils;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import io.minio.ObjectWriteResponse;
import lombok.extern.slf4j.Slf4j;

/**
 * @author Domenic
 * @Classname MediaFileServiceImpl
 * @Description 媒资文件管理业务实现类 (小文件，不分片上传)
 * @Created by Domenic
 */
@Slf4j
@Service
public class MediaFileServiceImpl implements MediaFileService {

    @Autowired
    private MediaFileMapper mediaFileMapper;

    @Autowired
    private FileDbUtils fileDbUtils;

    @Autowired
    private MinioUtils minioUtils;

    /**
     * 存储普通文件的桶
     */
    @Value("${minio.bucket.files}")
    private String bucket;

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
        Long total = pageResult.getTotal();

        log.debug("查询媒资文件列表成功, 数据总数={}, 匹配条件 - 文件名称={}; 文件类型={}; 文件审核状态={};", total, dto.getFilename(), dto.getFileType(), dto.getAuditStatus());

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
        String mimeType = FileUtils.getMimeTypeFromExt(ext);

        // 文件存储路径 (年/月/日)
        String defaultFolderPath = getDefaultFolderPath();

        // 文件 MD5 值
        String fileMd5 = FileUtils.getFileMd5(new File(tempFilePath));

        // 最终的文件名
        String finalFilename = filename.substring(0, filename.lastIndexOf(".")) + fileMd5 + ext;

        // 最终的文件存放路径 (路径 + 文件名)
        String objectName = defaultFolderPath + finalFilename;

        // 将上传的文件信息添加到数据库的文件信息表中
        MediaFile resDb = fileDbUtils.addFileInfo(companyId, fileMd5, dto, bucket, finalFilename, objectName);

        if (resDb != null) {
            // 上传文件到 minio
            ObjectWriteResponse resp = minioUtils.uploadFile(tempFilePath, mimeType, bucket, objectName);

            // 准备返回的对象
            if (resp != null) {
                FileResultDto resDto = new FileResultDto();
                BeanUtils.copyProperties(resDb, resDto);
                return resDto;
            }
        }
        return null;
    }

    @Override
    public FileResultDto deleteMediaFile(Long companyId, FileParamsDto dto) {

        // 从数据库中获取文件信息
        List<MediaFile> fileList = fileDbUtils.getListFileInfo(companyId, dto, bucket, null);

        MediaFile fileToDelete = null;

        // 若该文件存在且仅有一条该文件记录，则删除该信息
        if (fileList.size() == 1) {
            // 获取文件信息
            fileToDelete = fileList.get(0);

            // 删除数据库中的文件信息
            if (fileDbUtils.deleteFileInfo(fileToDelete)) {
                // 将文件从 minio 中删除
                if (minioUtils.deleteFile(bucket, fileToDelete.getFilePath())) {
                    // 返回 FileResultDto 对象
                    FileResultDto resDto = new FileResultDto();
                    BeanUtils.copyProperties(fileToDelete, resDto);
                    return resDto;
                }
            }
            XueChengEduException.cast("从数据库删除文件信息失败");
        } else if (fileList.size() == 0) {
            log.error("从数据库删除文件信息失败，数据库中没有该文件 ({}) 的记录, bucket={}", dto.getFilename(), bucket);
            XueChengEduException.cast("从数据库删除文件信息失败，无效文件!");
        } else {
            log.error("从数据库删除文件信息失败，数据库中有 {} 条该文件 ({}) 的记录, bucket={}", fileList.size(), dto.getFilename(), bucket);
            XueChengEduException.cast("从数据库删除文件信息失败，未知错误!");
        }

        return null;
    }

    /**
     * 获取文件在 minio 中的默认存储路径，示例：年/月/日
     * @return 文件夹路径
     */
    private String getDefaultFolderPath() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        return sdf.format(new Date()).replace("-", "/") + "/";
    }

}