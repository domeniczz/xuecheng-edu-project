package com.xuecheng.content.service;

import com.xuecheng.base.model.ResponseResult;
import com.xuecheng.content.model.dto.SaveTeachplanDto;
import com.xuecheng.content.model.dto.TeachplanDto;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * @author Domenic
 * @Classname TeachplanServiceTest
 * @Description 教学计划（章节）测试类
 * @Created by Domenic
 */
@SpringBootTest
class TeachplanServiceTest {

    @Autowired
    private TeachplanService teachplanService;

    @Test
    void queryTreeNodes() {
        long courseId = 117L;
        List<TeachplanDto> teachplanTreeList = teachplanService.queryTreeNodes(courseId);
        Assertions.assertNotNull(teachplanTreeList);

        System.out.println("\n===================================================");
        teachplanTreeList.forEach(System.out::println);
        System.out.println("===================================================\n");
    }

    @Test
    void addTeachplan() {
        SaveTeachplanDto dto = new SaveTeachplanDto();
        dto.setCourseId(74L);
        dto.setParentid(293L);
        dto.setGrade(1);
        dto.setPname("新增章节3");
        ResponseResult resp = teachplanService.saveTeachplan(dto);
        System.out.println(resp);
    }

    @Test
    void updateTeachplan() {
        SaveTeachplanDto dto = new SaveTeachplanDto();
        dto.setId(251L);
        dto.setCourseId(74L);
        dto.setParentid(0L);
        dto.setGrade(1);
        dto.setPname("新增章节");
        ResponseResult resp = teachplanService.saveTeachplan(dto);
        System.out.println(resp);
    }

    @Test
    void deleteTeachplan() {
        long id = 293L;
        ResponseResult resp = teachplanService.deleteTeachplan(id);
        System.out.println(resp);
    }

    @Test
    void moveUp() {
        long id = 296L;
        ResponseResult resp = teachplanService.moveUp(id);
        System.out.println(resp);
    }

    @Test
    void moveDown() {
        long id = 296L;
        ResponseResult resp = teachplanService.moveDown(id);
        System.out.println(resp);
    }

}