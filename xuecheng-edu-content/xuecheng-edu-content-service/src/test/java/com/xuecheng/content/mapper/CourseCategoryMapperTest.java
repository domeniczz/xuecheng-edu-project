package com.xuecheng.content.mapper;

import com.xuecheng.content.model.dto.CourseCategoryTreeDto;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

/**
 * @author Domenic
 * @Classname CourseCategoryMapperTest
 * @Description 课程分类服务测试类
 * @Created by Domenic
 */
@SpringBootTest
public class CourseCategoryMapperTest {

    @Autowired
    private CourseCategoryMapper courseCategoryMapper;

    @Test
    void testCourseCategoryMapper() {
        List<CourseCategoryTreeDto> courseCategoryTreeList = courseCategoryMapper.selectTreeNodes("1");
        Assertions.assertNotNull(courseCategoryTreeList);

        System.out.println("\n===================================================");
        courseCategoryTreeList.forEach(System.out::println);
        System.out.println("===================================================\n");
    }

}
