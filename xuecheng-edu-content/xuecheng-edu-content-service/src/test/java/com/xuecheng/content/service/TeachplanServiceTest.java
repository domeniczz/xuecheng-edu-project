package com.xuecheng.content.service;

import com.xuecheng.base.model.RestResponse;
import com.xuecheng.content.model.dto.SaveTeachplanDto;
import com.xuecheng.content.model.dto.TeachplanDto;
import com.xuecheng.content.model.po.Teachplan;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Domenic
 * @Classname TeachplanServiceTest
 * @Description 教学计划 (章节) 测试类
 * @Created by Domenic
 */
@SpringBootTest
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class TeachplanServiceTest {

    @Autowired
    private TeachplanService teachplanService;

    private static long courseId;
    private static final int PARENT_NUM = 2; // 父章节数量
    private static final int CHILDREN_NUM = PARENT_NUM * 2 - 1; // 子章节数量
    private static List<SaveTeachplanDto> dtoParentList;
    private static List<SaveTeachplanDto> dtoChildrenList;

    @BeforeAll
    static void setUp() {
        courseId = 1000000L;

        dtoParentList = new ArrayList<>();
        dtoChildrenList = new ArrayList<>();

        SecureRandom random = new SecureRandom();

        // 初始化父章节
        for (int i = 0; i < PARENT_NUM; i++) {
            SaveTeachplanDto dto = new SaveTeachplanDto();
            dto.setCourseId(courseId);
            dto.setParentid(0L);
            dto.setGrade(1);
            dto.setPname("测试章节 Unit Test-" + random.nextInt(100));
            dtoParentList.add(dto);
        }

        // 初始化子章节
        for (int i = 0; i < CHILDREN_NUM; i++) {
            SaveTeachplanDto dto = new SaveTeachplanDto();
            dto.setCourseId(courseId);
            // 父章节的 ID 在创建父章节后设置
            dto.setParentid(0L);
            dto.setPname("测试子章节 Unit Test-" + random.nextInt(100));
            dto.setGrade(2);
            dtoChildrenList.add(dto);
        }
    }

    @Test
    @Order(2)
    void testQueryTreeNodes() {
        List<TeachplanDto> teachplanTreeList = teachplanService.queryTreeNodes(courseId);
        Assertions.assertNotNull(teachplanTreeList);
        Assertions.assertTrue(!teachplanTreeList.isEmpty(), "章节信息查询结果为空");
    }

    @Test
    @Order(1)
    void testCreateTeachplan() {
        // 创建父章节
        dtoParentList.forEach(dto -> {
            Teachplan res = teachplanService.saveTeachplan(dto);
            Assertions.assertNotNull(res);
            // 保存父章节的 ID
            dto.setId(res.getId());
        });

        // 第一个父章节下只有一个子节点，其他的有两个子节点
        double index = 0.5;
        // 创建子章节
        for (SaveTeachplanDto dto : dtoChildrenList) {
            // 设置父章节的 ID
            dto.setParentid(dtoParentList.get((int) Math.floor(index)).getId());
            // 使每个父章节下最多有两个子章节
            index += 0.5;
            Teachplan res = teachplanService.saveTeachplan(dto);
            Assertions.assertNotNull(res);
            // 保存子章节的 ID，供其他 test 方法使用
            dto.setId(res.getId());
        }
    }

    @Test
    @Order(3)
    void testUpdateTeachplan() {
        // 使用 create 方法创建的课程的 ID
        dtoParentList.forEach(dto -> {
            Teachplan res = teachplanService.saveTeachplan(dto);
            Assertions.assertNotNull(res);
        });
        dtoChildrenList.forEach(dto -> {
            Teachplan res = teachplanService.saveTeachplan(dto);
            Assertions.assertNotNull(res);
        });
    }

    @Test
    @Order(6)
    void testDeleteTeachplan() {
        // 删除子章节
        RestResponse<Object> res1 = teachplanService.deleteTeachplan(dtoChildrenList.get(0).getId());
        Assertions.assertNotNull(res1);
        // 删除父章节
        RestResponse<Object> res2 = teachplanService.deleteTeachplan(dtoParentList.get(0).getId());
        Assertions.assertNotNull(res2);
    }

    @Test
    @Order(7)
    void testDeleteAll() {
        // 删除所有章节
        RestResponse<Object> resp = teachplanService.deleteAll(1000000L);
        Assertions.assertNotNull(resp);
    }

    @Test
    @Order(4)
    void testMoveUp() {
        RestResponse<Object> res1 = teachplanService.moveUp(dtoParentList.get(1).getId());
        Assertions.assertNotNull(res1);
        RestResponse<Object> res2 = teachplanService.moveUp(dtoChildrenList.get(2).getId());
        Assertions.assertNotNull(res2);
    }

    @Test
    @Order(5)
    void testMoveDown() {
        RestResponse<Object> res1 = teachplanService.moveUp(dtoParentList.get(0).getId());
        Assertions.assertNotNull(res1);
        RestResponse<Object> res2 = teachplanService.moveUp(dtoChildrenList.get(1).getId());
        Assertions.assertNotNull(res2);
    }

}