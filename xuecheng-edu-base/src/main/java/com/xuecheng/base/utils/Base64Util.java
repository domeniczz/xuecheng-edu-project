package com.xuecheng.base.utils;

/**
 * @author Domenic
 * @Classname Base64Util
 * @Description Base64 工具类
 * @Created by Domenic
 */
public class Base64Util {

    /*
     * Base64 encoding is commonly used when there is a need to encode binary data, especially when 
     * that data needs to be stored and transferred over media that are designed to deal with text.
     * This encoding helps to ensure that the data remains intact without modification during transport.
     * 
     * Here are some reasons why programmers might use Base64 encoding:
     * 
     * 1. Binary data safety:
     *    Base64 is commonly used when there is a need to encode binary data,
     *    particularly when that data needs to be transferred over media that are designed to handle ASCII.
     *    It is designed to carry data stored in binary formats across channels that only reliably support text.
     * 
     * 2. No data loss:
     *    When you encode data in ASCII, you start losing unsupported characters, however, Base64 encoding is
     *    lossless - meaning it ensures that the data remains intact without modification during transport.
     * 
     * 3. Embedding binary data in text documents:
     *    Base64 is used commonly in a number of applications including email via MIME,
     *    as well as storing complex data in XML or JSON.
     * 
     * 4. URL Encoding:
     *    Base64 is used when you want to encode something which might be included in a URL.
     *    Some characters in the URL are reserved and if used as data, it might cause the URL to be incorrectly interpreted.
     *    Base64 ensures the data remains unchanged without regard to the context in which they are used.
     * 
     * 5. File Conversion:
     *    It's also used for reading binary files as text files.
     *    This is particularly useful when you need to send a file or files
     *    and you only have text-based protocols at your disposal.
     */

    private static final char LAST_2_BYTE = (char) Integer.parseInt("00000011", 2);
    private static final char LAST_4_BYTE = (char) Integer.parseInt("00001111", 2);
    private static final char LAST_6_BYTE = (char) Integer.parseInt("00111111", 2);

    private static final char LEAD_2_BYTE = (char) Integer.parseInt("11000000", 2);
    private static final char LEAD_4_BYTE = (char) Integer.parseInt("11110000", 2);
    private static final char LEAD_6_BYTE = (char) Integer.parseInt("11111100", 2);

    private static final char[] ENCODE_TABLE = new char[] {
            'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z',
            'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z',
            '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', '+', '/' };

    private Base64Util() {
        // prevents other classes from instantiating it
    }

    /**
     * Encodes a given byte array using a variant of Base64 encoding
     * @param input byte array to encode
     * @return encoded string
     */
    public static String encode(byte[] input) {
        StringBuilder builder = new StringBuilder((int) (input.length * 1.34D) + 3);
        processInputBytes(input, builder);
        addPadding(builder);
        return builder.toString();
    }

    private static void processInputBytes(byte[] input, StringBuilder builder) {
        int num = 0;
        int bits = 8;
        int step = 6;

        for (int i = 0; i < input.length; ++i) {
            num %= bits;
            char currentByte = processByte(input, num, i);
            builder.append(ENCODE_TABLE[currentByte]);
            num += step;
        }
    }

    private static char processByte(byte[] input, int num, int i) {
        char currentByte = 0;

        switch (num) {
            /*
             * if num is 0, we are looking at the first 6 bits of the current byte
             * we mask the byte with 0b11000000 (the LEAD_6_BYTE constant) to keep only the first 6 bits,
             * and then we shift right by 2 positions to get our base64 index
             */
            case 0:
                currentByte = (char) ((input[i] & LEAD_6_BYTE) >>> 2);
                break;
            /*
             * if num is 2, we are looking at the last 6 bits of the current byte
             * we mask the byte with 0b00111111 (the LAST_6_BYTE constant) to keep only the last 6 bits
             */
            case 2:
                currentByte = (char) (input[i] & LAST_6_BYTE);
                break;
            /*
             * if num is 4, we are looking at the last 4 bits of the current byte and the first 2 bits of the next byte
             * we mask the byte with 0b00001111 (the LAST_4_BYTE constant) to keep only the last 4 bits,
             * and then we shift left by 2 positions
             */
            case 4:
                currentByte = (char) ((input[i] & LAST_4_BYTE) << 2);
                // if there is a next byte, we take the first 2 bits of the next byte and add it to our current base64 index
                if (i + 1 < input.length) {
                    currentByte |= (input[i + 1] & LEAD_2_BYTE) >>> 6;
                }
                break;
            /*
             * if num is 6, we are looking at the last 2 bits of the current byte and the first 4 bits of the next byte
             * we mask the byte with 0b00000011 (the LAST_2_BYTE constant) to keep only the last 2 bits,
             * and then we shift left by 4 positions
             */
            case 6:
                currentByte = (char) ((input[i] & LAST_2_BYTE) << 4);
                if (i + 1 < input.length) {
                    currentByte |= (input[i + 1] & LEAD_4_BYTE) >>> 4;
                }
                break;
            default:
                break;
        }

        return currentByte;
    }

    private static void addPadding(StringBuilder builder) {
        int padding = 4 - builder.length() % 4;
        for (int i = padding; i > 0; --i) {
            builder.append("=");
        }
    }

}
