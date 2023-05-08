package com.xuecheng.media.api;

import com.xuecheng.base.exception.XueChengEduException;
import com.xuecheng.base.model.RestResponse;
import com.xuecheng.media.model.dto.FileParamsDto;
import com.xuecheng.media.service.BigFilesService;
import com.xuecheng.media.utils.TempFileUtils;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

/**
 * @author Domenic
 * @Classname BigFilesController
 * @Description 大文件上传接口 (分块上传)
 * @Created by Domenic
 */
@Api(value = "大文件上传接口", tags = "大文件上传接口")
@RestController
public class BigFilesController {

    @Autowired
    private BigFilesService bigFilesService;

    @ApiOperation(value = "文件上传前，检查文件是否已存在")
    @PostMapping("/upload/checkfile")
    public RestResponse<Boolean> checkfile(@RequestParam("fileMd5") String fileMd5) throws Exception {
        return bigFilesService.checkFile(fileMd5);
    }

    @ApiOperation(value = "分块文件上传前，检查文件是否已存在")
    @PostMapping("/upload/checkchunk")
    public RestResponse<Boolean> checkchunk(@RequestParam("fileMd5") String fileMd5,
            @RequestParam("chunk") int chunkIndex) throws Exception {
        return bigFilesService.checkChunk(fileMd5, chunkIndex);
    }

    @ApiOperation(value = "上传分块文件")
    @PostMapping("/upload/uploadchunk")
    public RestResponse<?> uploadchunk(@RequestParam("file") MultipartFile file,
            @RequestParam("fileMd5") String fileMd5,
            @RequestParam("chunk") int chunk) throws Exception {

        // 将数据转存为本地临时文件
        String tempFilePath = TempFileUtils.storeAsTempFile(file);

        try {
            // 调用 service 上传分块文件
            return bigFilesService.uploadChunk(fileMd5, chunk, tempFilePath);
        } catch (Exception e) {
            XueChengEduException.cast(e.getMessage());
            return null;
        } finally {
            // 删除临时文件
            TempFileUtils.deleteTempFile(tempFilePath);
        }
    }

    @ApiOperation(value = "合并文件")
    @PostMapping("/upload/mergechunks")
    public RestResponse<?> mergechunks(@RequestParam("fileMd5") String fileMd5,
            @RequestParam("fileName") String fileName,
            @RequestParam("chunkTotal") int chunkTotal) throws Exception {

        // TODO: 5/2/2023 10:13 PM 通过单点登录系统，获取到用户所属机构的 ID
        Long companyId = 1232141425L;

        // 文件信息对象
        FileParamsDto dto = new FileParamsDto();
        dto.setFilename(fileName);
        dto.setTags("视频文件");
        dto.setFileType("001002");

        return bigFilesService.mergeChunksAndSave(companyId, fileMd5, chunkTotal, dto);
    }

}
