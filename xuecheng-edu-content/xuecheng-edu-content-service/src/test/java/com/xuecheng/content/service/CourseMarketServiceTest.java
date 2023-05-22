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

    private static CourseMarket courseMarket;

    private ThreadLocal<Long> courseId = new ThreadLocal<>();

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
    public void testQuery() {
        CourseMarket res = courseMarketService.query(courseId.get());
        Assertions.assertNotNull(res);
        Assertions.assertEquals("201001", res.getCharge(), "课程营销信息查询结果不正确");
    }

    @Test
    @Order(1)
    public void testCreateCourseMarket() {
        CourseMarket res = courseMarketService.saveCourseMarket(courseMarket);
        Assertions.assertNotNull(res);
        courseId.set(res.getId());
        Assertions.assertTrue(courseId.get() > 0, "课程营销信息创建失败, ID 无效");
    }

    @Test
    @Order(3)
    public void testUpdateCourseMarket() {
        courseMarket.setId(courseId.get());
        CourseMarket res = courseMarketService.saveCourseMarket(courseMarket);
        Assertions.assertNotNull(res);
    }

    @Test
    @Order(4)
    public void testDelete() {
        int res = courseMarketService.delete(courseId.get());
        Assertions.assertEquals(1, res);
    }

}
