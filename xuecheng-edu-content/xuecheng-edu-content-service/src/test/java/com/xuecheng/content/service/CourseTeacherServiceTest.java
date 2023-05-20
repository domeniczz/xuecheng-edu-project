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

    private static long COURSE_ID;
    private static CourseTeacher COURSE_TEACHER;

    @BeforeAll
    static void setUp() {
        COURSE_ID = 1000001L;

        COURSE_TEACHER = new CourseTeacher();
        COURSE_TEACHER.setCourseId(COURSE_ID);
        COURSE_TEACHER.setTeacherName("测试老师 Unit Test");
        COURSE_TEACHER.setPosition("测试职位 Unit Test");
        COURSE_TEACHER.setIntroduction("测试测试测试测试测试测试");
        COURSE_TEACHER.setPhotograph("https://www.test.com/test.jpg");
    }

    @Test
    @Order(2)
    void testQueryTeacherList() {
        List<CourseTeacher> res = courseTeacherService.queryTeacherList(COURSE_ID);
        Assertions.assertNotNull(res);

        System.out.println("\n===================================================");
        res.forEach(System.out::println);
        System.out.println("===================================================\n");
    }

    @Test
    @Order(1)
    void testCreateTeacher() {
        CourseTeacher res = courseTeacherService.save(COURSE_TEACHER);
        Assertions.assertNotNull(res);
        Assertions.assertEquals(COURSE_ID, res.getCourseId());

        System.out.println("\n===================================================\n"
                + res
                + "\n===================================================\n");
    }

    @Test
    @Order(3)
    void testUpdateTeacher() {
        CourseTeacher res = courseTeacherService.save(COURSE_TEACHER);
        Assertions.assertNotNull(res);

        System.out.println("\n===================================================\n"
                + res
                + "\n===================================================\n");
    }

    @Test
    @Order(4)
    void testDelete() {
        RestResponse<Object> res = courseTeacherService.delete(COURSE_ID, COURSE_TEACHER.getId());
        Assertions.assertNotNull(res);
    }

    @Test
    @Order(5)
    void testDeleteAll() {
        RestResponse<Object> res = courseTeacherService.deleteAll(COURSE_ID);
        Assertions.assertNotNull(res);
    }

}
