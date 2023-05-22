package com.xuecheng.content.service;

import com.xuecheng.base.model.RestResponse;
import com.xuecheng.content.model.po.CourseTeacher;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

/**
 * @author Domenic
 * @Classname CourseTeacherService
 * @Description 课程师资管理服务测试类
 * @Created by Domenic
 */
@SpringBootTest
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class CourseTeacherServiceTest {

    @Autowired
    private CourseTeacherService courseTeacherService;

    private static long courseId;
    private static CourseTeacher courseTeacher;

    @BeforeAll
    static void setUp() {
        courseId = 1000001L;

        courseTeacher = new CourseTeacher();
        courseTeacher.setCourseId(courseId);
        courseTeacher.setTeacherName("测试老师 Unit Test");
        courseTeacher.setPosition("测试职位 Unit Test");
        courseTeacher.setIntroduction("测试测试测试测试测试测试");
        courseTeacher.setPhotograph("https://www.test.com/test.jpg");
    }

    @Test
    @Order(2)
    void testQueryTeacherList() {
        List<CourseTeacher> res = courseTeacherService.queryTeacherList(courseId);
        Assertions.assertNotNull(res);
        Assertions.assertTrue(!res.isEmpty(), "课程师资信息查询结果为空");
    }

    @Test
    @Order(1)
    void testCreateTeacher() {
        CourseTeacher res = courseTeacherService.save(courseTeacher);
        Assertions.assertNotNull(res);
        Assertions.assertEquals(courseId, res.getCourseId());
    }

    @Test
    @Order(3)
    void testUpdateTeacher() {
        CourseTeacher res = courseTeacherService.save(courseTeacher);
        Assertions.assertNotNull(res);
    }

    @Test
    @Order(4)
    void testDelete() {
        RestResponse<Object> res = courseTeacherService.delete(courseId, courseTeacher.getId());
        Assertions.assertNotNull(res);
    }

    @Test
    @Order(5)
    void testDeleteAll() {
        RestResponse<Object> res = courseTeacherService.deleteAll(courseId);
        Assertions.assertNotNull(res);
    }

}
