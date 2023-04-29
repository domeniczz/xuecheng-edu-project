package com.xuecheng.media.service;

import com.xuecheng.base.model.PageParams;
import com.xuecheng.base.model.PageResult;
import com.xuecheng.media.model.dto.QueryMediaParamsDto;
import com.xuecheng.media.model.po.MediaFile;
import com.xuecheng.media.model.dto.FileParamsDto;
import com.xuecheng.media.model.dto.FileResultDto;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Objects;

/**
 * @author Domenic
 * @Classname MediaFileServiceTest
 * @Description 媒资文件管理业务测试类
 * @Created by Domenic
 */
@SpringBootTest
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class MediaFileServiceTest {

    @Autowired
    private MediaFileService mediaFileService;

    private static Long companyId;
    private static String filename;
    private static String fileType;
    private static String filePath;
    private static String auditStatus;
    private static QueryMediaParamsDto queryMediaParamsDto;
    private static FileParamsDto fileParamsDto;

    @BeforeAll
    static void init() {
        companyId = 100011L;

        // 路径：target/test-classes，substring 是为了去除 "file:/" 前缀
        String basePath = Objects.requireNonNull(MediaFileServiceTest.class.getResource("/")).toString().substring(6);

        filename = "bootstrap.yml";
        // 文件类型：其他
        fileType = "001003";
        // 文件在本地的路径
        filePath = basePath + filename;
        // 审核状态：已通过
        auditStatus = "002003";

        fileParamsDto = new FileParamsDto();
        fileParamsDto.setFilename(filename);
        fileParamsDto.setFileType(fileType);
        fileParamsDto.setFileSize(100L);
        fileParamsDto.setTags("测试文件 Unit Test");
        fileParamsDto.setUsername("测试测试");
        fileParamsDto.setRemark("测试测试");
    }

    @Test
    @Order(2)
    void test_queryMediaFileList() {
        // 分页参数
        PageParams pageParams = new PageParams();
        // 当前页码
        pageParams.setPageNo(1L);
        // 每页最多记录数
        pageParams.setPageSize(2L);

        // 媒资管理请求参数
        queryMediaParamsDto = new QueryMediaParamsDto();
        // 文件名称
        queryMediaParamsDto.setFilename(filename);
        // 文件类型：图片
        queryMediaParamsDto.setFileType(fileType);
        // 审核状态：已通过
        queryMediaParamsDto.setAuditStatus(auditStatus);

        PageResult<MediaFile> res = mediaFileService.queryMediaFileList(companyId, pageParams, queryMediaParamsDto);

        // 更新文件路径 (变为在文件服务器中的路径)
        filePath = res.getItems().get(0).getFilePath();

        System.out.println("\n===================================================");
        res.getItems().forEach(System.out::println);
        System.out.println("===================================================\n");
    }

    @Test
    @Order(1)
    void test_uploadMediaFile() {
        FileResultDto resDto = mediaFileService.uploadMediaFile(companyId, fileParamsDto, filePath);

        // 更新文件名
        filename = resDto.getFilename();
        fileParamsDto.setFilename(filename);

        System.out.println("\n===================================================\n"
                + resDto
                + "\n===================================================\n");
    }

    @Test
    @Order(3)
    void test_deleteMediaFile() {
        // 若测试失败，可能是因为 bucket Access Policy 是 private，需要改为 public
        mediaFileService.deleteMediaFile(companyId, fileParamsDto);
    }

}
