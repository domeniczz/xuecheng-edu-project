package com.xuecheng.content;

import com.xuecheng.content.model.dto.CourseCategoryTreeDto;
import com.xuecheng.content.service.CourseCategoryService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

/**
 * @author Domenic
 * @Classname courseCategoryServiceTest
 * @Description TODO
 * @Date 4/11/2023 9:24 PM
 * @Created by Domenic
 */
@SpringBootTest
public class CourseCategoryServiceTest {

    @Autowired
    CourseCategoryService courseCategoryService;

    @Test
    void testcourseCategoryService() {
        List<CourseCategoryTreeDto> courseCategoryTreeList = courseCategoryService.queryTreeNodes("1");
        Assertions.assertNotNull(courseCategoryTreeList);

        System.out.println("\n===================================================");
        courseCategoryTreeList.forEach(System.out::println);
        System.out.println("===================================================\n");
    }

}
