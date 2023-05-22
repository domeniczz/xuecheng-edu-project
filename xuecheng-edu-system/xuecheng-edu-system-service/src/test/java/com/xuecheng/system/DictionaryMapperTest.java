package com.xuecheng.system;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.xuecheng.system.mapper.DictionaryMapper;
import com.xuecheng.system.model.po.Dictionary;

import org.junit.jupiter.api.Assertions;
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
    private DictionaryMapper dictionaryMapper;

    @Test
    void testQueryAll() {
        // 查询全部
        List<Dictionary> dictionaryList = dictionaryMapper.selectList(null);
        Assertions.assertNotNull(dictionaryList);
        Assertions.assertTrue(!dictionaryList.isEmpty());
    }

    @Test
    void testGetByCode() {
        Dictionary dictItem = dictionaryMapper.selectOne(
                new LambdaQueryWrapper<Dictionary>().eq(Dictionary::getCode, "200"));
        Assertions.assertNotNull(dictItem);
    }

}
