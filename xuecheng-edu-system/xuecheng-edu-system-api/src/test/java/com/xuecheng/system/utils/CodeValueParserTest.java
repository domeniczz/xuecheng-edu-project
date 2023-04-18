package com.xuecheng.system.utils;

import com.fasterxml.jackson.core.JsonProcessingException;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.Map;

/**
 * @author Domenic
 * @Classname CodeValueParserTest
 * @Description Code 解析器测试类
 * @Created by Domenic
 */
public class CodeValueParserTest {
    @Test
    void testParseItemValues() {
        String jsonString = "{\"id\":1,\"name\":\"学历\",\"code\":\"education\",\"itemValues\":[{\"code\":\"200001\",\"desc\":\"低级\"},{\"code\":\"200002\",\"desc\":\"中级\"},{\"code\":\"200003\",\"desc\":\"高级\"}]}";
        try {
            Map<String, String> itemValues = CodeValueParser.parseItemValues(jsonString);
            Assertions.assertNotNull(itemValues);
            itemValues.forEach((k, v) -> System.out.println(k + " : " + v));
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
    }
}
