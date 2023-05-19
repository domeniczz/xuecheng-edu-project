package com.xuecheng.base.utils;

import java.net.InetAddress;
import java.net.UnknownHostException;

import javax.servlet.http.HttpServletRequest;

import lombok.extern.slf4j.Slf4j;

/**
 * @author Domenic
 * @Classname IpUtil
 * @Description IP 工具类
 * @Created by Domenic
 */
@Slf4j
public class IpUtil {

    /**
     * 可能包含客户端 IP 地址的头信息
     */
    private static final String[] IP_HEADER_CANDIDATES = {
            "x-forwarded-for",
            "Proxy-Client-IP",
            "WL-Proxy-Client-IP",
            "HTTP_CLIENT_IP",
            "HTTP_X_FORWARDED_FOR",
            "X-Real-IP" };

    private IpUtil() {
        // prevents other classes from instantiating it
    }

    /**
     * 获取服务器发出请求的客户端的 IP 地址<br/>
     * 因为客户端可能通过 HTTP 代理或负载均衡器，连接到 Web 服务器<br/>
     * 由于 Web 路由和代理的潜在复杂性，本方法考虑了可能找到客户端 IP 地址的各种场景
     * @param request 请求
     * @return 客户端 IP 地址
     */
    public static String getIpAddr(HttpServletRequest request) {

        /*
         * 遍历提供的请求头，尝试获取客户端 IP
         * - 若找到标头 "x-forwarded-for"，且其值不是 "unknown"，则能从中直接提取出 IP 地址
         * - 若标头中有多个 IP 地址 (由于多个代理)，会使用第一个，因为这通常是原始客户端的 IP
         * - 若没有找到标头 x-forwarded-for，则尝试获取请求头中其他有可能包含客户端原始 IP 的标头
         */
        for (String header : IP_HEADER_CANDIDATES) {
            String ip = extractIpFromHeader(request.getHeader(header));
            if (ip != null) {
                log.debug("通过请求头 {} 获取客户端 IP 地址 {}", header, ip);
                return ip;
            }
        }

        String ip = request.getRemoteAddr();

        String loopbackIpv4 = "127.0.0.1";
        String loopbackIpv6 = "0:0:0:0:0:0:1";

        if (ip.equals(loopbackIpv4) || ip.endsWith(loopbackIpv6)) {
            // 若 IP 是本地的，则获取服务器网卡 (NIC) 的 IP 地址
            try {
                InetAddress inet = InetAddress.getLocalHost();
                // 根据网卡获取本机的 IP
                ip = inet.getHostAddress();
                log.debug("检测到 IP 是本地的，通过网卡获取本地 IP 地址 {}", ip);
                return ip;
            } catch (UnknownHostException e) {
                log.debug("检测到 IP 是本地的，但是获取本地 IP 地址出错");
                e.printStackTrace();
            }
        }

        return ip;
    }

    /**
     * 从给定的请求头中提取 IP 地址
     * @param header 请求头字符串
     * @return 请求头中找到的 IP 地址值，若请求头为 空 或 {@code unknown}，则返回 {@code null}
     */
    private static String extractIpFromHeader(String header) {
        String unknown = "unknown";
        String ipSeparator = ",";

        if (StringUtil.isNotEmpty(header) && !unknown.equalsIgnoreCase(header)) {
            // 多次反向代理后会有多个 IP 值，第一个才是客户端原始 IP
            if (header.contains(ipSeparator)) {
                return header.split(",")[0];
            } else {
                return header;
            }
        }

        return null;
    }

}
