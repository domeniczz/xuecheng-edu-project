package com.xuecheng.content.service;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * @author Domenic
 * @Classname TeachplanMediaServiceTest
 * @Description 教学计划媒资关联信息服务测试类
 * @Created by Domenic
 */
@SpringBootTest
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
// 暂时不使用该测试类 (还未完成)
@Disabled
public class TeachplanMediaServiceTest {

    @Autowired
    private TeachplanMediaService teachplanMediaService;

    @Test
    void test_deleteTeachplanMedia() {
        int res = teachplanMediaService.deleteTeachplanMedia(1000001L);
        Assertions.assertEquals(1, res);
    }

}
