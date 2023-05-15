package com.xuecheng.base.utils;

import java.util.regex.Pattern;

/**
 * @author Domenic
 * @Classname PhoneUtil
 * @Description 手机号工具类
 * @Created by Domenic
 */
public class PhoneUtil {

    private static final Pattern PHONE_PATTERN = Pattern.compile("^((13[0-9])|(14[5|7])|(15([0-3]|[5-9]))|(17[013678])|(18[0,5-9]))\\d{8}$");

    /**
     * 校验用户手机号是否合法
     * @param phone 手机号
     * @return {@code true} or {@code false}
     */
    public static Boolean isMatches(String phone) {
        return PHONE_PATTERN.matcher(phone).matches();
    }

}
