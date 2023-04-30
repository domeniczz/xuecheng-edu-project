package com.xuecheng.system;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.xuecheng.system.mapper.DictionaryMapper;
import com.xuecheng.system.model.po.Dictionary;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

/**
 * @author Domenic
 * @Classname DictionaryMapperTest
 * @Description DictionaryMapper 测试类
 * @Created by Domenic
 */
@SpringBootTest
public class DictionaryMapperTest {

    @Autowired
    DictionaryMapper dictionaryMapper;

    @Test
    void test_dictionaryMapper() {

        // 查询全部
        List<Dictionary> dictionaryList = dictionaryMapper.selectList(null);

        System.out.println("\n===================================");
        dictionaryList.forEach(System.out::println);
        System.out.println("===================================\n");

    }

    @Test
    void test_queryAll() {
        // 查询全部
        List<Dictionary> dictionaryList = dictionaryMapper.selectList(null);

        System.out.println("\n===================================");
        dictionaryList.forEach(System.out::println);
        System.out.println("===================================\n");
    }

    @Test
    void test_getByCode() {
        Dictionary dictItem = dictionaryMapper.selectOne(new LambdaQueryWrapper<Dictionary>().eq(Dictionary::getCode, "200"));
        System.out.println("\n===================================================\n"
                + dictItem
                + "\n===================================================\n");
    }

}
