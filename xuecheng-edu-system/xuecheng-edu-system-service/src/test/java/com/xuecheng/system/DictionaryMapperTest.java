package com.xuecheng.system;

import com.xuecheng.system.mapper.DictionaryMapper;
import com.xuecheng.system.model.po.Dictionary;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

/**
 * @author Domenic
 * @Classname DictionaryMapperTest
 * @Description TODO
 * @Date 4/10/2023 7:33 PM
 * @Created by Domenic
 */
@SpringBootTest
public class DictionaryMapperTest {

    @Autowired
    DictionaryMapper dictionaryMapper;

    @Test
    void testDictionaryMapper() {

        // 查询全部
        List<Dictionary> dictionaryList = dictionaryMapper.selectList(null);

        System.out.println("\n===================================");
        dictionaryList.forEach(System.out::println);
        System.out.println("===================================\n");

    }

}
