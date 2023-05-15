package com.xuecheng.base.utils;

import java.security.SecureRandom;

/**
 * @author Domenic
 * @Classname RandomStringUtil
 * @Description 随机字符串工具
 * @Created by Domenic
 */
public class RandomStringUtil {

    /**
     * 获取指定长度随机字符串
     * @param length 指定长度
     * @return 随机字符串
     */
    public static String getRandomString(int length) {
        SecureRandom random = new SecureRandom();
        StringBuilder builder = new StringBuilder(length);

        for (int i = 0; i < length; ++i) {
            int number = random.nextInt(3);
            switch (number) {
                case 0:
                    // Generate a random uppercase letter
                    builder.append((char) ('A' + random.nextInt(26)));
                    break;
                case 1:
                    // Generate a random lowercase letter
                    builder.append((char) ('a' + random.nextInt(26)));
                    break;
                case 2:
                    // Generate a random digit
                    builder.append(random.nextInt(10));
                    break;
                default:
                    break;
            }
        }

        return builder.toString();
    }

}
