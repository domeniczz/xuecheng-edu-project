package com.xuecheng.media.api;

import com.xuecheng.base.exception.XueChengEduException;
import com.xuecheng.base.model.PageParams;
import com.xuecheng.base.model.PageResult;
import com.xuecheng.media.model.dto.QueryMediaParamsDto;
import com.xuecheng.media.model.dto.FileParamsDto;
import com.xuecheng.media.model.dto.FileResultDto;
import com.xuecheng.media.model.po.MediaFile;
import com.xuecheng.media.service.MediaFileService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;

/**
 * @author Domenic
 * @Classname MediaFileController
 * @Description 媒资文件管理接口
 * @Created by Domenic
 */
@Api(value = "媒资文件管理接口", tags = "媒资文件管理接口")
@Slf4j
@RestController
public class MediaFileController {

    @Autowired
    MediaFileService mediaFileService;

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
    public FileResultDto upload(@RequestPart("filedata") MultipartFile filedata) throws IOException {

        // TODO: 4/27/2023 6:36 PM 通过单点登录系统，获取到用户所属机构的 ID
        Long companyId = 1232141425L;
        // String companyName = "机构名称";

        // 封装上传文件的信息到 DTO
        FileParamsDto dto = new FileParamsDto();
        // 原始文件名称
        dto.setFilename(filedata.getOriginalFilename());
        // 文件大小
        dto.setFileSize(filedata.getSize());
        // 文件类型：图片
        dto.setFileType(getFileType(filedata));
        // 上传人
        // dto.setUsername("");

        // 数据转存到本地临时文件
        String tempFilePath = storeAsTempFile(filedata);

        try {
            // 调用 service 上传图片文件
            return mediaFileService.uploadMediaFile(companyId, dto, tempFilePath);
        } catch (Exception e) {
            XueChengEduException.cast(e.getMessage());
            return null;
        } finally {
            // 删除临时文件
            deleteTempFile(tempFilePath);
        }
    }

    @PostMapping(value = "/delete/coursefile")
    public FileResultDto delete(@RequestBody FileParamsDto dto) throws IOException {

        // TODO: 4/27/2023 6:36 PM 通过单点登录系统，获取到用户所属机构的 ID
        long companyId = 1232141425L;

        return mediaFileService.deleteMediaFile(companyId, dto);
    }

    /**
    * 将给定的 multipart 文件存储为临时文件，并返回临时文件的绝对路径
    * @param filedata multipart 文件
    * @return 临时文件的绝对路径
    * @throws IOException 创建临时文件时的 IO 异常
    */
    private String storeAsTempFile(MultipartFile filedata) throws IOException {
        // 创建一个临时文件
        File tempFile = File.createTempFile("upload-file-", ".temp");
        filedata.transferTo(tempFile);

        // 返回临时文件路径
        return tempFile.getAbsolutePath();
    }

    /**
    * 判断给定的 MultipartFile 的文件类型
    * @param file MultipartFile 文件
    * @return 表示文件类型的编码 ("001001" - 图片，"001002" - 视频，"001003" - 其他，null - 未知)
    */
    private String getFileType(MultipartFile file) {
        // 获取文件的 MIME 类型
        String contentType = file.getContentType();

        String imageType = "image/";
        String videoType = "video/";

        if (contentType != null) {
            if (contentType.startsWith(imageType)) {
                // 图片
                return "001001";
            } else if (contentType.startsWith(videoType)) {
                // 视频
                return "001002";
            } else {
                // 其他
                return "001003";
            }
        }

        return null;
    }

    /**
     * 删除临时文件
     * @param tempFilePath 临时文件的绝对路径
     */
    private void deleteTempFile(String tempFilePath) {
        if (!new File(tempFilePath).delete()) {
            log.error("删除临时文件 {} 失败", tempFilePath);
        }
    }

}