package com.xuecheng.system.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Domenic
 * @Classname CodeValueParser
 * @Description 解析查询得到的 Dictionary 中的 itemValues 字段，得到对应的 code 和 description
 * @Created by Domenic
 */
public class CodeValueParser {

    /**
     * 解析 JSON 字符串 (itemValues)，得到 code 和 description
     * @param jsonString JSON 字符串
     * @param size 预估的 item 数量
     * @return Map<String, String>，key 为 code，value 为 description
     * @throws JsonProcessingException JSON 解析异常
     */
    public static Map<String, String> parseItemValues(String jsonString, int size) throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode rootNode = objectMapper.readTree(jsonString);
        JsonNode itemValuesNode = rootNode.path("itemValues");

        Map<String, String> resultMap = new HashMap<>(size);

        if (itemValuesNode.isArray()) {
            for (JsonNode node : itemValuesNode) {
                String code = node.path("code").asText();
                String desc = node.path("desc").asText();
                resultMap.put(code, desc);
            }
        }
        return resultMap;
    }

}
