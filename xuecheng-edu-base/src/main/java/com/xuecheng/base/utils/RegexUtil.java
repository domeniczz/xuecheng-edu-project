package com.xuecheng.base.utils;

import java.util.regex.Pattern;

/**
 * @author Domenic
 * @Classname PhoneUtil
 * @Description 手机号工具类
 * @Created by Domenic
 */
public class RegexUtil {

    private static final Pattern PHONE_PATTERN = Pattern.compile("^((13\\d)|(14[5|7])|(15([0-35-9]))|(17[013678])|(18[0,5-9]))\\d{8}$");

    private static final Pattern QQ_PATTERN = Pattern.compile("^[1-9]\\d{4,10}$");

    private RegexUtil() {
        // prevents other classes from instantiating it
    }

    /**
     * 校验 手机号 是否合法
     * @param phone 手机号
     * @return {@code true} or {@code false}
     */
    public static Boolean isPhoneValid(String phone) {
        return PHONE_PATTERN.matcher(phone).matches();
    }

    /**
     * 校验 QQ 号 是否合法
     * @param phone 手机号
     * @return {@code true} or {@code false}
     */
    public static Boolean isQqValid(String qq) {
        return QQ_PATTERN.matcher(qq).matches();
    }

}
