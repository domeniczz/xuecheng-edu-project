package com.xuecheng.content.service;

import com.xuecheng.content.model.dto.CourseCategoryTreeDto;
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
public class CourseCategoryServiceTest {

    @Autowired
    private CourseCategoryService courseCategoryService;

    @Test
    void testcourseCategoryService() {
        List<CourseCategoryTreeDto> courseCategoryTreeList = courseCategoryService.queryTreeNodes("1");
        Assertions.assertNotNull(courseCategoryTreeList);

        System.out.println("\n===================================================");
        courseCategoryTreeList.forEach(System.out::println);
        System.out.println("===================================================\n");
    }

}
