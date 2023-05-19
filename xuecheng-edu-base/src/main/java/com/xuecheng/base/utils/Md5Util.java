package com.xuecheng.base.utils;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * @author Domenic
 * @Classname Md5Util
 * @Description MD5 工具类
 * @Created by Domenic
 */
public class Md5Util {

    private Md5Util() {
        // prevents other classes from instantiating it
    }

    /**
     * 为字符串生成 MD5 值 (32 位字符的十六进制表示)
     * @param plainText 待生成 MD5 的字符串
     * @return MD5 值
     */
    public static String get32Md5(String plainText) {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");

            // MD5 (Message Digest algorithm 5) produces a 128-bit (16-byte) hash value,
            // which means the size of byte array is 16.
            byte[] encodedhash = md.digest(plainText.getBytes(StandardCharsets.UTF_8));

            // Because each byte is represented by two hexadecimal characters,
            // we will get a 32 characters long string.
            return bytesToHex(encodedhash);
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalArgumentException("No such algorithm! " + e.getMessage());
        }
    }

    /**
     * 为字符串生成 MD5 值 (16 位字符的十六进制表示)
     * @param plainText 待生成 MD5 的字符串
     * @return MD5 值
     */
    public static String get16Md5(String plainText) {
        /*
         * The 16-character hexadecimal MD5 representation is typically just the 
         * middle 16 characters of the full 32-character representation.
         * 
         * Thus, we can just return the middle 16 characters of the full hex string
         */
        return get32Md5(plainText).substring(8, 24);
    }

    /**
     * 将 {@link Byte} 数组转换为十六进制字符串
     * @param hash {@link Byte} 数组
     * @return
     */
    private static String bytesToHex(byte[] byteArr) {
        StringBuilder hexString = new StringBuilder(2 * byteArr.length);

        for (byte b : byteArr) {
            /*
             * "0xff & b" converts `byte` to `int`, and deals with the negative condition of `byte`.
             * 
             * How it works?
             * 
             * - In Java, `byte` is a signed 8-bit type, meaning it can represent values from -128 to 127.
             *   When a byte is converted to an `int` (a 32-bit type), if the `byte` is negative,
             *   it will be sign-extended to fill 32 bits, which means its sign bit will be copied into the higher order bits
             *   of the `int`, making the `int` negative and much larger (when viewed as unsigned) than 255.
             * 
             * - However, when we do bitwise AND (&) operation with "0xff" (255 in decimal),
             *   it effectively masks the higher order 24 bits of the `int` to be 0, and keeps only the lower 8 bits, which is what we want.
             *   This gives us a `int` that values between 0 and 255, regardless of whether the original `byte` value was negative or positive.
             * 
             * After getting the `int` value from `byte`, we convert it to hexadecimal string.
             */
            String hex = Integer.toHexString(0xff & b);
            /*
             * Each byte is represented by two hexadecimal characters,
             * so we need to check the length of the string.
             */
            if (hex.length() == 1) {
                hexString.append('0');
            }
            hexString.append(hex);
        }

        return hexString.toString();
    }

}
