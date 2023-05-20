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

    private static long COMPANY_ID;
    private static AddCourseDto ADD_DTO;
    private static UpdateCourseDto UPDATE_DTO;
    private static long ADDED_ID = -1;

    @BeforeAll
    static void setUp() {
        COMPANY_ID = 10000L;

        ADD_DTO = new AddCourseDto();
        ADD_DTO.setName("测试课程 Unit Test");
        ADD_DTO.setUsers("测试测试测试测试测试测试");
        ADD_DTO.setTags("测试测试测试测试测试测试");
        ADD_DTO.setMt("1-1");
        ADD_DTO.setSt("1-1-1");
        ADD_DTO.setGrade("200002");
        ADD_DTO.setTeachmode("200002");
        ADD_DTO.setDescription("测试测试测试测试测试测试");
        ADD_DTO.setPic("https://www.unit-test.com/test.jpg");
        ADD_DTO.setCharge("201001");
        ADD_DTO.setPrice(100.01);
        ADD_DTO.setOriginalPrice(200.02);
        ADD_DTO.setQq("1893827187");
        ADD_DTO.setWechat("test_wechat_id");
        ADD_DTO.setPhone("13657485768");
        ADD_DTO.setValidDays(100);

        UPDATE_DTO = new UpdateCourseDto();
        BeanUtils.copyProperties(ADD_DTO, UPDATE_DTO);
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
        Assertions.assertTrue(res.getItems().size() > 0);

        System.out.println("\n===================================================");
        res.getItems().forEach(System.out::println);
        System.out.println("===================================================\n");
    }

    @Test
    @Order(3)
    void testQueryCourseBaseAndMarketInfoById() {
        // 使用 create 方法创建的课程的 ID
        CourseBaseInfoDto res = courseBaseInfoService.queryCourseBaseAndMarketInfoById(ADDED_ID);
        Assertions.assertNotNull(res);
    }

    @Test
    @Order(2)
    void testCreate() {
        CourseBaseInfoDto res = courseBaseInfoService.create(COMPANY_ID, ADD_DTO);
        Assertions.assertNotNull(res);

        // 获取 ID 给 update 测试方法使用
        ADDED_ID = res.getId();

        System.out.println("\n===================================================\n"
                + res
                + "\n===================================================\n");
    }

    @Test
    @Order(4)
    void testUpdate() {
        // 使用 create 方法创建的课程的 ID
        UPDATE_DTO.setId(ADDED_ID);
        CourseBaseInfoDto res = courseBaseInfoService.update(COMPANY_ID, UPDATE_DTO);
        Assertions.assertNotNull(res);

        System.out.println("\n===================================================\n"
                + res
                + "\n===================================================\n");
    }

    @Test
    @Order(5)
    void testDelete() {
        // 使用 create 方法创建的课程的 ID
        RestResponse<Object> res = courseBaseInfoService.delete(ADDED_ID);
        Assertions.assertEquals(0, res.getCode());
    }

}