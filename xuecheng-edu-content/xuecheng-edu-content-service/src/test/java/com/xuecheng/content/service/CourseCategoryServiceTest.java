package com.xuecheng.content.service;

import com.xuecheng.content.model.dto.CourseCategoryTreeDto;
import com.xuecheng.content.model.po.CourseCategory;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * @author Domenic
 * @Classname courseCategoryServiceTest
 * @Description 课程分类服务测试类
 * @Created by Domenic
 */
@SpringBootTest
// @Transactional(rollbackFor = Exception.class)
public class CourseCategoryServiceTest {

    @Autowired
    private CourseCategoryService courseCategoryService;

    @Test
    void test_queryTreeNodes() {
        List<CourseCategoryTreeDto> res = courseCategoryService.queryTreeNodes();
        Assertions.assertNotNull(res);

        System.out.println("\n===================================================");
        res.forEach(System.out::println);
        System.out.println("===================================================\n");
    }

    @Test
    void test_query() {
        String categoryId = "1-1";
        CourseCategory res = courseCategoryService.query(categoryId);
        Assertions.assertNotNull(res);

        System.out.println("\n===================================================\n"
                + res
                + "\n===================================================\n");
    }

}
