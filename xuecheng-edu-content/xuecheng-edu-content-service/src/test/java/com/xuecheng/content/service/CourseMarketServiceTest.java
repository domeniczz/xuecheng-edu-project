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

    private static long COURSE_ID;
    private static CourseMarket COURSE_MARKET;

    @BeforeAll
    static void setUp() {
        COURSE_MARKET = new CourseMarket();
        COURSE_MARKET.setCharge("201001");
        COURSE_MARKET.setPrice(299.99);
        COURSE_MARKET.setOriginalPrice(399.99);
        COURSE_MARKET.setQq("1925768576");
        COURSE_MARKET.setWechat("test_wechat_id");
        COURSE_MARKET.setPhone("13758475860");
        COURSE_MARKET.setValidDays(321);
    }

    @Test
    @Order(2)
    public void testQuery() {
        CourseMarket courseMarket = courseMarketService.query(COURSE_ID);
        Assertions.assertNotNull(courseMarket);

        System.out.println("\n===================================================\n"
                + courseMarket
                + "\n===================================================\n");
    }

    @Test
    @Order(1)
    public void testCreateCourseMarket() {
        CourseMarket res = courseMarketService.saveCourseMarket(COURSE_MARKET);
        COURSE_ID = res.getId();
        Assertions.assertNotNull(res);
    }

    @Test
    @Order(3)
    public void testUpdateCourseMarket() {
        COURSE_MARKET.setId(COURSE_ID);
        CourseMarket res = courseMarketService.saveCourseMarket(COURSE_MARKET);
        Assertions.assertNotNull(res);
    }

    @Test
    @Order(4)
    public void testDelete() {
        int res = courseMarketService.delete(COURSE_ID);
        Assertions.assertEquals(1, res);
    }

}
