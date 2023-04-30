package com.xuecheng.content.service;

import com.xuecheng.content.model.po.CourseMarket;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * @author Domenic
 * @Classname CourseMarketServiceTest
 * @Description 课程营销信息服务测试类
 * @Created by Domenic
 */
@SpringBootTest
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class CourseMarketServiceTest {

    @Autowired
    private CourseMarketService courseMarketService;

    private static long courseId;
    private static CourseMarket courseMarket;

    @BeforeAll
    static void setUp() {
        courseMarket = new CourseMarket();
        courseMarket.setCharge("201001");
        courseMarket.setPrice(299.99);
        courseMarket.setOriginalPrice(399.99);
        courseMarket.setQq("1925768576");
        courseMarket.setWechat("test_wechat_id");
        courseMarket.setPhone("13758475860");
        courseMarket.setValidDays(321);
    }

    @Test
    @Order(2)
    public void test_query() {
        CourseMarket courseMarket = courseMarketService.query(courseId);
        Assertions.assertNotNull(courseMarket);

        System.out.println("\n===================================================");
        System.out.println(courseMarket);
        System.out.println("===================================================\n");
    }

    @Test
    @Order(1)
    public void test_createCourseMarket() {
        CourseMarket res = courseMarketService.saveCourseMarket(courseMarket);
        courseId = res.getId();
        Assertions.assertNotNull(res);
    }

    @Test
    @Order(3)
    public void test_updateCourseMarket() {
        courseMarket.setId(courseId);
        CourseMarket res = courseMarketService.saveCourseMarket(courseMarket);
        Assertions.assertNotNull(res);
    }

    @Test
    @Order(4)
    public void test_delete() {
        int res = courseMarketService.delete(courseId);
        Assertions.assertEquals(1, res);
    }

}
