package com.xuecheng.media.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xuecheng.base.exception.XueChengEduException;
import com.xuecheng.base.model.PageParams;
import com.xuecheng.base.model.PageResult;
import com.xuecheng.base.utils.FileUtil;
import com.xuecheng.media.mapper.MediaFileMapper;
import com.xuecheng.media.model.dto.FileParamsDto;
import com.xuecheng.media.model.dto.FileResultDto;
import com.xuecheng.media.model.dto.QueryMediaParamsDto;
import com.xuecheng.media.model.po.MediaFile;
import com.xuecheng.media.operations.FileInfoDbOperation;
import com.xuecheng.media.operations.MinioOperation;
import com.xuecheng.media.service.MediaFileService;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.List;

import io.minio.ObjectWriteResponse;
import lombok.extern.slf4j.Slf4j;

/**
 * @author Domenic
 * @Classname MediaFileServiceImpl
 * @Description 媒资文件管理接口实现类 (小文件，不分片上传)
 * @Created by Domenic
 */
@Slf4j
@Service
public class MediaFileServiceImpl implements MediaFileService {

    @Autowired
    private MediaFileMapper mediaFileMapper;

    @Autowired
    private FileInfoDbOperation fileInfoDbOperation;

    @Autowired
    private MinioOperation minioOperation;

    /**
     * 存储普通文件的桶
     */
    @Value("${minio.bucket.files}")
    private String bucket;

    @Override
    public PageResult<MediaFile> queryMediaFileList(Long companyId, PageParams pageParams, QueryMediaParamsDto dto) {

        LambdaQueryWrapper<MediaFile> queryWrapper = new LambdaQueryWrapper<>();

        // 匹配文件名称
        queryWrapper.like(StringUtils.isNotEmpty(dto.getFilename()),
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
        String ext = FileUtil.getFileExtension(filename);

        // 文件 mimeType
        String mimeType = FileUtil.getMimeTypeFromExt(ext);

        // 文件存储路径 (年/月/日)
        String defaultFolderPath = getFolderPathByDate(true, true, true);

        // 文件 MD5 值
        String fileMd5 = FileUtil.getFileMd5(Paths.get(tempFilePath));

        // 最终的文件名
        String finalFilename = FileUtil.dropFileExtension(filename) + fileMd5 + ext;

        // 最终的文件存放路径 (路径 + 文件名)
        String objectName = defaultFolderPath + finalFilename;

        MediaFile mediaFileToAdd = new MediaFile();
        BeanUtils.copyProperties(dto, mediaFileToAdd);
        mediaFileToAdd.setId(fileMd5);
        mediaFileToAdd.setCompanyId(companyId);
        mediaFileToAdd.setBucket(bucket);
        mediaFileToAdd.setFilename(finalFilename);
        mediaFileToAdd.setFilePath(objectName);

        // 将上传的文件信息添加到数据库的文件信息表中
        MediaFile resDb = fileInfoDbOperation.addFileInfo(mediaFileToAdd);

        if (resDb != null) {
            // 上传文件到 minio
            ObjectWriteResponse resp = minioOperation.uploadFile(tempFilePath, mimeType, bucket, objectName);

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

        MediaFile mediaFileToDelete = new MediaFile();
        BeanUtils.copyProperties(dto, mediaFileToDelete);
        mediaFileToDelete.setCompanyId(companyId);
        mediaFileToDelete.setBucket(bucket);

        // 尝试从数据库中获取文件信息
        List<MediaFile> fileList = fileInfoDbOperation.getListFileInfo(mediaFileToDelete);

        if (fileList.isEmpty()) {
            log.error("从数据库删除文件信息失败，数据库中没有该文件 ({}) 的记录, bucket={}", dto.getFilename(), bucket);
            XueChengEduException.cast("从数据库删除文件信息失败，无效文件!");
            return null;
        }

        if (fileList.size() > 1) {
            log.error("从数据库删除文件信息失败，数据库中有 {} 条该文件 ({}) 的记录, bucket={}", fileList.size(), dto.getFilename(), bucket);
            XueChengEduException.cast("从数据库删除文件信息失败，未知错误!");
            return null;
        }

        MediaFile fileToDelete = fileList.get(0);

        // 删除数据库中的文件信息
        boolean dbRes = fileInfoDbOperation.deleteFileInfo(fileToDelete);
        if (!dbRes) {
            XueChengEduException.cast("从数据库删除文件信息失败");
            return null;
        }

        // 将文件从 minio 中删除
        boolean minioRes = minioOperation.deleteFile(bucket, fileToDelete.getFilePath());
        if (!minioRes) {
            XueChengEduException.cast("从 Minio 中删除文件信息失败");
            return null;
        }

        // 返回 FileResultDto 对象
        FileResultDto resDto = new FileResultDto();
        BeanUtils.copyProperties(fileToDelete, resDto);
        return resDto;

    }

    /**
     * 根据日期生成路径，示例：年/月/日
     * @param year 路径是否包含 年
     * @param month 路径是否包含 月
     * @param day 路径是否包含 日
     * @return 文件夹路径
     */
    public static String getFolderPathByDate(boolean year, boolean month, boolean day) {
        LocalDate now = LocalDate.now();

        StringBuilder path = new StringBuilder();

        if (year) {
            path.append(now.getYear());
        }

        if (month && path.length() > 0) {
            path.append('/');
            path.append(String.format("%02d", now.getMonthValue()));
        }

        if (day && path.length() > 0) {
            path.append('/');
            path.append(String.format("%02d", now.getDayOfMonth()));
        }

        return path.append('/').toString();
    }

}