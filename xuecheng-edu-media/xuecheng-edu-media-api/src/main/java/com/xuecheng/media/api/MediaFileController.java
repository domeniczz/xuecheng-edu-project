package com.xuecheng.media.api;

import com.xuecheng.base.exception.XueChengEduException;
import com.xuecheng.base.model.PageParams;
import com.xuecheng.base.model.PageResult;
import com.xuecheng.media.model.dto.FileParamsDto;
import com.xuecheng.media.model.dto.FileResultDto;
import com.xuecheng.media.model.dto.QueryMediaParamsDto;
import com.xuecheng.media.model.po.MediaFile;
import com.xuecheng.media.service.MediaFileService;
import com.xuecheng.media.utils.FileUtils;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

/**
 * @author Domenic
 * @Classname MediaFileController
 * @Description 媒资文件管理接口
 * @Created by Domenic
 */
@Api(value = "媒资文件管理接口", tags = "媒资文件管理接口")
@RestController
public class MediaFileController {

    @Autowired
    private MediaFileService mediaFileService;

    @ApiOperation("媒资文件列表分页查询")
    @PostMapping("/files")
    public PageResult<MediaFile> list(PageParams pageParams, @RequestBody QueryMediaParamsDto queryMediaParamsDto) {
        // 通过单点登录系统，获取到用户所属机构的 ID
        // 为了方便测试，这里先写死
        // TODO: 4/27/2023 6:36 PM 通过单点登录系统，获取到用户所属机构的 ID
        Long companyId = 1232141425L;

        return mediaFileService.queryMediaFileList(companyId, pageParams, queryMediaParamsDto);
    }

    @ApiOperation("上传文件 (非视频)")
    @PostMapping(value = "/upload/coursefile", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public FileResultDto upload(@RequestPart("filedata") MultipartFile file) throws IOException {

        // TODO: 4/27/2023 6:36 PM 通过单点登录系统，获取到用户所属机构的 ID
        Long companyId = 1232141425L;
        // String companyName = "机构名称";

        // 封装上传文件的信息到 DTO
        FileParamsDto dto = new FileParamsDto();
        // 原始文件名称
        dto.setFilename(file.getOriginalFilename());
        // 文件大小
        dto.setFileSize(file.getSize());
        // 文件类型：图片
        dto.setFileType(FileUtils.getFileType(file));
        // 上传人
        // dto.setUsername("");

        // 将数据转存为本地临时文件
        String tempFilePath = FileUtils.storeAsTempFile(file);

        try {
            // 调用 service 上传文件
            return mediaFileService.uploadMediaFile(companyId, dto, tempFilePath);
        } catch (Exception e) {
            XueChengEduException.cast(e.getMessage());
            return null;
        } finally {
            // 删除临时文件
            FileUtils.deleteTempFile(tempFilePath);
        }
    }

    @PostMapping(value = "/delete/coursefile")
    public FileResultDto delete(@RequestBody FileParamsDto dto) throws IOException {

        // TODO: 4/27/2023 6:36 PM 通过单点登录系统，获取到用户所属机构的 ID
        long companyId = 1232141425L;

        return mediaFileService.deleteMediaFile(companyId, dto);
    }

}