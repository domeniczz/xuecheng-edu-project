package com.xuecheng.content.service;

import com.xuecheng.base.model.PageParams;
import com.xuecheng.base.model.PageResult;
import com.xuecheng.base.model.ResponseResult;
import com.xuecheng.content.model.dto.AddCourseDto;
import com.xuecheng.content.model.dto.CourseBaseInfoDto;
import com.xuecheng.content.model.dto.QueryCourseParamsDto;
import com.xuecheng.content.model.dto.UpdateCourseDto;
import com.xuecheng.content.model.po.CourseBase;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * @author Domenic
 * @Classname CourseBaseMapperTest
 * @Description 课程信息服务测试类
 * @Created by Domenic
 */
@SpringBootTest
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class CourseBaseInfoServiceTest {

    @Autowired
    private CourseBaseInfoService courseBaseInfoService;

    private static long companyId;
    private static AddCourseDto addCourseDto;
    private static UpdateCourseDto updateCourseDto;
    private static long addedCourseId = -1;

    @BeforeAll
    static void init() {
        companyId = 10000L;

        addCourseDto = new AddCourseDto();
        addCourseDto.setName("测试课程 Unit Test");
        addCourseDto.setUsers("测试测试测试测试测试测试");
        addCourseDto.setTags("测试测试测试测试测试测试");
        addCourseDto.setMt("1-1");
        addCourseDto.setSt("1-1-1");
        addCourseDto.setGrade("200002");
        addCourseDto.setTeachmode("200002");
        addCourseDto.setDescription("测试测试测试测试测试测试");
        addCourseDto.setPic("https://www.unit-test.com/test.jpg");
        addCourseDto.setCharge("201001");
        addCourseDto.setPrice(100.01);
        addCourseDto.setOriginalPrice(200.02);
        addCourseDto.setQq("1893827187");
        addCourseDto.setWechat("test_wechat_id");
        addCourseDto.setPhone("13657485768");
        addCourseDto.setValidDays(100);

        updateCourseDto = new UpdateCourseDto();
        BeanUtils.copyProperties(addCourseDto, updateCourseDto);
    }

    @Test
    @Order(1)
    void test_queryCourseBaseList() {

        // 分页参数
        PageParams pageParams = new PageParams();
        long pageNo = 1L; // 当前页码
        long pageSize = 3L; // 每页最多记录数
        pageParams.setPageNo(pageNo);
        pageParams.setPageSize(pageSize);

        // 查询条件
        QueryCourseParamsDto queryCourseParamsDto = new QueryCourseParamsDto();
        String courseName = "java";
        String auditStatus = "202004";
        String publishStatus = "203001";
        queryCourseParamsDto.setCourseName(courseName);
        queryCourseParamsDto.setAuditStatus(auditStatus);
        queryCourseParamsDto.setPublishStatus(publishStatus);

        PageResult<CourseBase> res = courseBaseInfoService.queryCourseBaseList(pageParams, queryCourseParamsDto);
        Assertions.assertNotNull(res);

        System.out.println("\n===================================================\n"
                + res
                + "\n===================================================\n");
    }

    @Test
    @Order(3)
    void test_queryCourseBaseAndMarketInfoById() {
        // 使用 create 方法创建的课程的 ID
        CourseBaseInfoDto res = courseBaseInfoService.queryCourseBaseAndMarketInfoById(addedCourseId);
        Assertions.assertNotNull(res);
        System.out.println("\n===================================================\n"
                + res
                + "\n===================================================\n");
    }

    @Test
    @Order(2)
    void test_create() {
        CourseBaseInfoDto res = courseBaseInfoService.create(companyId, addCourseDto);
        Assertions.assertNotNull(res);

        // 获取 ID 给 update 测试方法使用
        addedCourseId = res.getId();

        System.out.println("\n===================================================\n"
                + res
                + "\n===================================================\n");
    }

    @Test
    @Order(4)
    void test_update() {
        // 使用 create 方法创建的课程的 ID
        updateCourseDto.setId(addedCourseId);
        CourseBaseInfoDto res = courseBaseInfoService.update(companyId, updateCourseDto);
        Assertions.assertNotNull(res);

        System.out.println("\n===================================================\n"
                + res
                + "\n===================================================\n");
    }

    @Test
    @Order(5)
    void test_delete() {
        // 使用 create 方法创建的课程的 ID
        ResponseResult res = courseBaseInfoService.delete(addedCourseId);
        Assertions.assertNotNull(res);
    }

}