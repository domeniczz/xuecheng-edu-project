package com.xuecheng.base.utils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.serializer.SerializerFeature;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import lombok.extern.slf4j.Slf4j;

/**
 * @author Domenic
 * @Classname JsonUtil
 * @Description JSON 工具类
 * @Created by Domenic
 */
@Slf4j
public class JsonUtil {

    /**
     * 该参数设置 fastJson 如何序列化对象
     */
    private static final SerializerFeature[] FEATURES = { SerializerFeature.WriteDateUseDateFormat };

    private JsonUtil() {
        // prevents other classes from instantiating it
    }

    /**
     * 将 {@link Object} 转换为 JSON 字符串
     * @param object 待转换的 {@link Object}
     * @return 转换后的 JSON 字符串
     */
    public static String objectTojson(Object object) {
        // JSON.DEFFAULT_DATE_FORMAT is "yyyy-MM-dd HH:mm:ss"
        return JSON.toJSONString(object, FEATURES);
    }

    /**
     * 将 {@link List} 转换为 JSON 字符串
     * @param list 待转换的 {@link List}
     * @return 转换后的 JSON 字符串
     */
    public static String listTojson(List<Object> list) {
        return JSON.toJSONString(list, FEATURES);
    }

    /**
     * 字符串 JSON 格式转换为 {@link Map}
     * @param strJson 待转换的 JSON 字符串 {"username":"domenic"}
     * @return 根据 JSON 转换的 {@link Map}
     */
    public static Map<String, Object> jsonToMap(String strJson) {
        try {
            return JSON.<Map<String, Object>>parseObject(strJson, Map.class);
        } catch (JSONException e) {
            log.error("JSON 转换为 Map 出错, errorMsg={}" + e.getMessage());
            return Collections.emptyMap();
        }
    }

    /**
     * 字符串 JSON 格式转换为 {@link Object}
     * @param <T> 泛型
     * @param strJson 待转换的 JSON 字符串 {"username":"domenic"}
     * @param clazz 输出的类型
     * @return 根据 JSON 转换的 {@link Object}
     */
    public static <T> T jsonToObject(String strJson, Class<T> clazz) {
        try {
            return JSON.parseObject(strJson, clazz);
        } catch (JSONException e) {
            log.error("JSON 转换为 Object 出错, errorMsg={}" + e.getMessage());
        }
        return null;
    }

    /**
     * 字符串 JSON 转换为 {@link List}
     * @param strJson 待转换的 JSON 字符串 [{"username":"domenic"}]
     * @return 根据 JSON 转换的 {@link List}
     */
    public static <T> List<T> jsonToList(String strJson, Class<T> tClass) {
        try {
            return JSON.parseArray(strJson, tClass);
        } catch (JSONException e) {
            log.error("JSON 转换为 List 出错, errorMsg={}" + e.getMessage());
        }
        return Collections.emptyList();
    }

}
