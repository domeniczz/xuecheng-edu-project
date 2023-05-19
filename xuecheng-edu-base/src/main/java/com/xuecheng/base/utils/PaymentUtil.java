package com.xuecheng.base.utils;

import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.regex.Pattern;

/**
 * @author Domenic
 * @Classname PaymentUtil
 * @Description 付款工具类
 * @Created by Domenic
 */
public class PaymentUtil {

    private static final Pattern PATTERN = Pattern.compile("SJPAY(,\\S+){4}");
    public static final String PAY_PREFIX = "XC";

    private PaymentUtil() {
        // prevents other classes from instantiating it
    }

    public static boolean checkPayOrderAttach(String attach) {
        if (StringUtils.isBlank(attach)) {
            return false;
        }
        return PATTERN.matcher(attach).matches();
    }

    /**
    * Generates a unique pay order number by concatenating:
    * prefix,
    * the current date and time in the format 'yyMMddHHmmssSSS',
    * and a 15-character random alphanumeric string.
    *
    * @return  a unique pay order number
    */
    public static String genUniquePayOrderNumber() {
        String dateTime = DateTimeFormatter.ofPattern("yyMMddHHmmssSSS").format(LocalDateTime.now());
        return PAY_PREFIX + dateTime + RandomStringUtils.randomAlphanumeric(15);
    }

}
