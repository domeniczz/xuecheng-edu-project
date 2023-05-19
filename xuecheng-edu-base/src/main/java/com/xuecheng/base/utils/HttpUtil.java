package com.xuecheng.base.utils;

import com.alibaba.fastjson.JSON;
import com.xuecheng.base.model.RestResponse;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

/**
 * @author Domenic
 * @Classname HttpUtil
 * @Description HTTP 工具类
 * @Created by Domenic
 */
public class HttpUtil {

    private HttpUtil() {
        // prevents other classes from instantiating it
    }

    /**
     * 将错误响应以 JSON 字符串的形式写入 {@link HttpServletResponse} 对象
     * @param restResponse 包含错误信息的 {@link RestResponse} 对象
     * @param response 要写入错误的 {@link HttpServletResponse} 对象
     * @throws IOException 写入响应时发生 IO 异常
     */
    public static void writerError(RestResponse<?> restResponse, HttpServletResponse response) throws IOException {
        response.setContentType("application/json,charset=utf-8");
        response.setStatus(restResponse.getCode());
        JSON.writeJSONString(response.getOutputStream(), restResponse);
    }

    /**
     * 发送 POST 请求到指定 URL (给定参数) 并将响应作为字符串返回
     * @param generalUrl 请求的 URL
     * @param contentType 请求正文的内容类型
     * @param params 请求参数
     * @param encoding 请求正文的编码
     * @return 请求的响应
     * @throws Exception 发送请求时出错
     */
    public static String post(String generalUrl, String contentType, String params, String encoding) throws IOException {
        URL url = new URL(generalUrl);

        // 创建和 URL 之间的连接
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("POST");
        // 设置通用的请求属性
        connection.setRequestProperty("Content-Type", contentType);
        connection.setRequestProperty("Connection", "Keep-Alive");
        connection.setUseCaches(false);
        connection.setDoOutput(true);
        connection.setDoInput(true);
        connection.setConnectTimeout(20000);
        connection.setReadTimeout(20000);

        // 请求的输出流
        try (DataOutputStream out = new DataOutputStream(connection.getOutputStream())) {
            out.write(params.getBytes(encoding));
            out.flush();
        }

        // 建立连接
        connection.connect();

        // 使用输入流读取 URL 响应
        StringBuilder result = new StringBuilder();
        try (BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream(), encoding))) {
            String line;
            while ((line = in.readLine()) != null) {
                result.append(line);
            }

            connection.disconnect();
        }

        return result.toString();
    }

    /**
     * 根据提供的 API Key 和 Secret Key 生成并返回访问令牌 Token
     * @param apiKey API Key
     * @param secretKey Secret Key
     * @return 根据提供的 API Key 和 Secret Key 生成的访问令牌
     * @throws Exception 生成访问令牌时出错
     */
    public static String getAccessToken(String apiKey, String secretKey) throws IOException {
        // 生成 Token 地址
        String authHost = "https://aip.baidubce.com/oauth/2.0/token";
        String getAccessTokenUrl = authHost
                + "?"
                // 1. grant_type 为固定参数
                + "grant_type=client_credentials"
                // 2. 官网获取的 API Key
                + "&client_id=" + apiKey
                // 3. 官网获取的 Secret Key
                + "&client_secret=" + secretKey;

        URL realUrl = new URL(getAccessTokenUrl);

        // 创建和 URL 之间的连接
        HttpURLConnection connection = (HttpURLConnection) realUrl.openConnection();
        connection.setRequestMethod("GET");
        connection.connect();

        // 使用输入流读取 URL 的响应
        StringBuilder result = new StringBuilder();
        try (BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()))) {
            String line;
            while ((line = in.readLine()) != null) {
                result.append(line);
            }
        }

        connection.disconnect();

        Map<String, Object> resultMap = JsonUtil.jsonToMap(result.toString());
        return resultMap.get("access_token").toString();
    }

    /**
     * 发送 POST 请求到指定 URL (给定访问令牌, 内容类型, 参数)
     * @param requestUrl 请求的 URL
     * @param accessToken 访问令牌
     * @param params 请求参数
     * @return 请求的响应
     * @throws Exception 发送请求时出错
     */
    public static String postWithToken(String requestUrl, String accessToken, String params) throws IOException {
        // 这是在 HTTP 请求正文中发送数据时可以使用的内容类型之一
        // 它主要用于在 HTTP, POST, PUT 请求中提交表单数据
        String contentType = "application/x-www-form-urlencoded";
        return HttpUtil.post(requestUrl, accessToken, contentType, params);
    }

    /**
     * 发送 POST 请求到指定的 URL (给定访问令牌, 内容类型, 参数)
     * 若请求 URL 包含 "nlp"，则设置编码为 GBK
     * @param requestUrl 请求的 URL
     * @param accessToken 访问令牌
     * @param contentType 请求正文的内容类型
     * @param params 请求参数
     * @return 请求的响应
     * @throws Exception 发送请求时出错
     */
    public static String postWithToken(String requestUrl, String accessToken, String contentType, String params) throws IOException {
        String encoding = "UTF-8";

        String str = "nlp";
        if (requestUrl.contains(str)) {
            encoding = "GBK";
        }

        return HttpUtil.postWithToken(requestUrl, accessToken, contentType, params, encoding);
    }

    /**
     * 发送 POST 请求到指定 URL (给定访问令牌, 内容类型, 参数, 编码) 并将响应作为字符串返回
     * @param requestUrl 请求的 URL
     * @param accessToken 访问令牌
     * @param contentType 请求正文的内容类型
     * @param params 请求参数
     * @param encoding 请求正文的编码
     * @return 请求的响应
     * @throws Exception 发送请求时出错
     */
    public static String postWithToken(String requestUrl, String accessToken, String contentType, String params, String encoding) throws IOException {
        String url = requestUrl + "?access_token=" + accessToken;
        return HttpUtil.post(url, contentType, params, encoding);
    }

}
