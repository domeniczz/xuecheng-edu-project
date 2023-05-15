package com.xuecheng.base.utils;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.Base64;

import lombok.extern.slf4j.Slf4j;

/**
 * @author Domenic
 * @Classname EncryptUtil
 * @Description 编解码工具类
 * @Created by Domenic
 */
@Slf4j
public class EncryptUtil {

    /**
    * 将字节数组转换为 Base64 编码的字符串
    * @param bytes 要编码的字节数组
    * @return Base64 编码后的字符串
    */
    public static String encodeBase64(byte[] bytes) {
        String encoded = Base64.getEncoder().encodeToString(bytes);
        return encoded;
    }

    /**
    * 将 Base64 编码的字符串解码为字节数组
    * @param str 要解码的 Base64 编码字符串
    * @return 解码后的字节数组
    */
    public static byte[] decodeBase64(String str) {
        byte[] bytes = null;
        bytes = Base64.getDecoder().decode(str);
        return bytes;
    }

    /**
    * 以 UTF-8 编码格式对字符串进行 Base64 编码
    * @param str 需要进行编码的字符串
    * @return 编码后的字符串，若不支持 UTF-8 编码格式则返回 {@code null}
    */
    public static String encodeBase64Utf8(String str) {
        String encoded = null;

        try {
            encoded = Base64.getEncoder().encodeToString(str.getBytes("utf-8"));
        } catch (UnsupportedEncodingException e) {
            log.warn("不支持的编码格式, errorMsg={}", e.getMessage());
        }

        return encoded;
    }

    /**
    * 将 Base64 编码解码为 UTF-8 字符串
    * @param str 要解码的 Base64 编码字符串
    * @return 解码后的 UTF-8 字符串
    */
    public static String decodeBase64Utf8(String str) {
        String decoded = null;
        byte[] bytes = Base64.getDecoder().decode(str);

        try {
            decoded = new String(bytes, "utf-8");
        } catch (UnsupportedEncodingException e) {
            log.warn("不支持的编码格式, errorMsg={}", e.getMessage());
        }

        return decoded;
    }

    /**
    * 使用 UTF-8 编码 URL 字符串
    * @param url 要编码的字符串
    * @return 编码后的字符串，若编码失败则返回 {@code null}
    */
    public static String encodeUrl(String url) {
        String encoded = null;

        try {
            encoded = URLEncoder.encode(url, "utf-8");
        } catch (UnsupportedEncodingException e) {
            log.warn("URL 编码失败, errorMsg={}", e.getMessage());
        }

        return encoded;
    }

    /**
    * 使用 UTF-8 解码给定的 URL 字符串
    * @param url 要解码的 URL 字符串
    * @return 解码后的 URL 字符串
    */
    public static String decodeUrl(String url) {
        String decoded = null;

        try {
            decoded = URLDecoder.decode(url, "utf-8");
        } catch (UnsupportedEncodingException e) {
            log.warn("URLDecode失败, errorMsg={}", e.getMessage());
        }

        return decoded;
    }

}
