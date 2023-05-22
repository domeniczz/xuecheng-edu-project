package com.xuecheng.content.service;

import com.xuecheng.content.model.dto.CourseCategoryTreeDto;
import com.xuecheng.content.model.po.CourseCategory;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

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
    void testQueryTreeNodes() {
        List<CourseCategoryTreeDto> res = courseCategoryService.queryTreeNodes();
        Assertions.assertNotNull(res);
        Assertions.assertTrue(!res.isEmpty(), "课程分类查询结果为空");
    }

    @Test
    void testQuery() {
        String categoryId = "1-1";
        CourseCategory res = courseCategoryService.query(categoryId);
        Assertions.assertNotNull(res);
        Assertions.assertEquals(categoryId, res.getId(), "课程分类查询结果不正确");
    }

}
