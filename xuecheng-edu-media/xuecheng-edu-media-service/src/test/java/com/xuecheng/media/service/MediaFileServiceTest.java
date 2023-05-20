package com.xuecheng.media.service;

import com.xuecheng.base.model.PageParams;
import com.xuecheng.base.model.PageResult;
import com.xuecheng.media.model.dto.FileParamsDto;
import com.xuecheng.media.model.dto.FileResultDto;
import com.xuecheng.media.model.dto.QueryMediaParamsDto;
import com.xuecheng.media.model.po.MediaFile;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * @author Domenic
 * @Classname MediaFileServiceTest
 * @Description 媒资文件管理业务测试类
 * @Created by Domenic
 */
@SpringBootTest
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class MediaFileServiceTest {

    @Autowired
    private MediaFileService mediaFileService;

    private long companyId;
    /**
     * 测试文件名
     */
    @Value("${mediafile.file.name}")
    private String filename;
    /**
     * 测试文件路径
     */
    @Value("${mediafile.folder.path}")
    private String folderPath;
    private String filePath;
    private String fileType;
    private String auditStatus;
    private FileParamsDto dto;

    @BeforeAll
    void setUp() {

        // 机构 ID
        companyId = 100011L;
        // 文件路径
        filePath = folderPath + filename;
        // 文件类型：图片 (图片 001001; 视频 001002; 其他 001003)
        fileType = "001001";
        // 审核状态：已通过 (未通过 002001; 未审核 002002; 已通过 002003)
        auditStatus = "002003";
        // 文件大小 (乱写的)
        long fileSize = 100L;

        // 设置媒资文件操作请求参数 DTO
        dto = new FileParamsDto();
        dto.setFilename(filename);
        dto.setFileType(fileType);
        dto.setFileSize(fileSize);
        dto.setTags("测试文件 Unit Test");
        dto.setUsername("测试测试");
        dto.setRemark("测试测试");
    }

    @Test
    @Order(2)
    void testQueryMediaFileList() {
        // 分页参数
        PageParams pageParams = new PageParams();
        // 当前页码
        pageParams.setPageNo(1L);
        // 每页最多记录数
        pageParams.setPageSize(2L);

        // 媒资管理请求参数
        QueryMediaParamsDto queryMediaParamsDto = new QueryMediaParamsDto();
        // 文件名称
        queryMediaParamsDto.setFilename(dto.getFilename());
        // 文件类型：图片
        queryMediaParamsDto.setFileType(fileType);
        // 审核状态：已通过
        queryMediaParamsDto.setAuditStatus(auditStatus);

        PageResult<MediaFile> res = mediaFileService.queryMediaFileList(companyId, pageParams, queryMediaParamsDto);
        Assertions.assertNotNull(res, "查询媒资文件列表失败");
        Assertions.assertTrue(res.getItems().size() > 0, "媒资文件列表为空");

        // 更新文件路径 (变为在文件服务器中的路径)
        filePath = res.getItems().get(0).getFilePath();
    }

    @Test
    @Order(1)
    void testUploadMediaFile() {
        FileResultDto resDto = mediaFileService.uploadMediaFile(companyId, dto, filePath);
        Assertions.assertNotNull(resDto, "媒资文件上传失败");

        // 更新文件名
        filename = resDto.getFilename();
        dto.setFilename(filename);
    }

    @Test
    @Order(3)
    void testDeleteMediaFile() {
        // 若测试失败，可能是因为 bucket Access Policy 是 private，需要改为 public
        mediaFileService.deleteMediaFile(companyId, dto);
    }

}
