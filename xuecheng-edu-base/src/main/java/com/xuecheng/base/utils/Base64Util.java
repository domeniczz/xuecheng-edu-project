package com.xuecheng.base.utils;

/**
 * @author Domenic
 * @Classname Base64Util
 * @Description Base64 工具类
 * @Created by Domenic
 */
public class Base64Util {

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

    /**
     * Encodes a given byte array using a variant of Base64 encoding
     * @param input byte array to encode
     * @return encoded string
     */
    public static String encode(byte[] input) {
        /*
         * Creates a StringBuilder with an initial capacity based on the length of the input byte array.
         * 
         * The capacity is slightly larger than the length of the input array,
         * to account for padding and rounding errors in the base64 encoding process.
         */
        StringBuilder builder = new StringBuilder((int) (input.length * 1.34D) + 3);
        int num = 0;
        char currentByte = 0;
        int bits = 8;
        int step = 6;

        /*
         * This code is used to convert a byte array into a base64-encoded string.
         * 
         * It works by looping through each byte in the input array and breaking it up into 6-bit chunks.
         * Each chunk is then used to identify a corresponding character in the `ENCODE_TABLE` array,
         * which is added to a StringBuilder to create the final base64-encoded string.
         */
        for (int i = 0; i < input.length; ++i) {
            // process each byte in steps of 6 bits
            for (num %= bits; num < bits; num += step) {
                /*
                 * if num is 0, we are looking at the first 6 bits of the current byte
                 * we mask the byte with 0b11000000 (the LEAD_6_BYTE constant) to keep only the first 6 bits,
                 * and then we shift right by 2 positions to get our base64 index
                 */
                if (num == 0) {
                    currentByte = (char) ((input[i] & LEAD_6_BYTE) >>> 2);
                }
                /*
                 * if num is 2, we are looking at the last 6 bits of the current byte
                 * we mask the byte with 0b00111111 (the LAST_6_BYTE constant) to keep only the last 6 bits
                 */
                else if (num == 2) {
                    currentByte = (char) (input[i] & LAST_6_BYTE);
                }
                /*
                 * if num is 4, we are looking at the last 4 bits of the current byte and the first 2 bits of the next byte
                 * we mask the byte with 0b00001111 (the LAST_4_BYTE constant) to keep only the last 4 bits,
                 * and then we shift left by 2 positions
                 */
                else if (num == 4) {
                    currentByte = (char) ((input[i] & LAST_4_BYTE) << 2);
                    // if there is a next byte, we take the first 2 bits of the next byte and add it to our current base64 index
                    if (i + 1 < input.length) {
                        currentByte |= (input[i + 1] & LEAD_2_BYTE) >>> 6;
                    }
                }
                /*
                 * if num is 6, we are looking at the last 2 bits of the current byte and the first 4 bits of the next byte
                 * we mask the byte with 0b00000011 (the LAST_2_BYTE constant) to keep only the last 2 bits,
                 * and then we shift left by 4 positions
                 */
                else if (num == 6) {
                    currentByte = (char) ((input[i] & LAST_2_BYTE) << 4);
                    // if there is a next byte, we take the first 4 bits of the next byte and add it to our current base64 index
                    if (i + 1 < input.length) {
                        currentByte |= (input[i + 1] & LEAD_4_BYTE) >>> 4;
                    }
                }

                builder.append(ENCODE_TABLE[currentByte]);
            }
        }

        /*
         * Once all bytes have been processed, we need to add padding if necessary.
         * In Base64 encoding, the output is always a multiple of 4 characters.
         * So, if the length of our output is not a multiple of 4, we add padding with "=".
         */
        int padding = 4 - builder.length() % 4;
        for (int i = padding; i > 0; --i) {
            builder.append("=");
        }

        return builder.toString();
    }

}
