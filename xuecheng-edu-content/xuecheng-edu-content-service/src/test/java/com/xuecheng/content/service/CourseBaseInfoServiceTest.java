package com.xuecheng.content.service;

import com.xuecheng.base.model.PageParams;
import com.xuecheng.base.model.PageResult;
import com.xuecheng.base.model.RestResponse;
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
    private static AddCourseDto addDto;
    private static UpdateCourseDto updateDto;

    private ThreadLocal<Long> addedId = new ThreadLocal<>();

    @BeforeAll
    static void setUp() {
        companyId = 10000L;

        addDto = new AddCourseDto();
        addDto.setName("测试课程 Unit Test");
        addDto.setUsers("测试");
        addDto.setTags("测试");
        addDto.setMt("1-1");
        addDto.setSt("1-1-1");
        addDto.setGrade("200002");
        addDto.setTeachmode("200002");
        addDto.setDescription("测试");
        addDto.setPic("https://www.unit-test.com/test.jpg");
        addDto.setCharge("201001");
        addDto.setPrice(100.01);
        addDto.setOriginalPrice(200.02);
        addDto.setQq("1893827187");
        addDto.setWechat("test_wechat_id");
        addDto.setPhone("13657485768");
        addDto.setValidDays(100);

        updateDto = new UpdateCourseDto();
        BeanUtils.copyProperties(addDto, updateDto);
    }

    @Test
    @Order(1)
    void testQueryCourseBaseList() {

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
        Assertions.assertTrue(!res.getItems().isEmpty(), "查询结果为空");
    }

    @Test
    @Order(3)
    void testQueryCourseBaseAndMarketInfoById() {
        // 使用 create 方法创建的课程的 ID
        CourseBaseInfoDto res = courseBaseInfoService.queryCourseBaseAndMarketInfoById(addedId.get());
        Assertions.assertNotNull(res);
    }

    @Test
    @Order(2)
    void testCreate() {
        CourseBaseInfoDto res = courseBaseInfoService.create(companyId, addDto);
        Assertions.assertNotNull(res);

        // 获取 ID 给 update 测试方法使用
        addedId.set(res.getId());
        Assertions.assertTrue(addedId.get() > 0, "课程 ID 无效");
    }

    @Test
    @Order(4)
    void testUpdate() {
        // 使用 create 方法创建的课程的 ID
        updateDto.setId(addedId.get());
        CourseBaseInfoDto res = courseBaseInfoService.update(companyId, updateDto);
        Assertions.assertNotNull(res);
    }

    @Test
    @Order(5)
    void testDelete() {
        // 使用 create 方法创建的课程的 ID
        RestResponse<Object> res = courseBaseInfoService.delete(addedId.get());
        Assertions.assertEquals(0, res.getCode());
    }

}