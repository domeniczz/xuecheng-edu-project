package com.xuecheng.base.utils;

import java.io.File;
import java.nio.charset.Charset;
import java.util.UUID;
import java.util.regex.Pattern;

/**
 * @author Domenic
 * @Classname StringUtil
 * @Description 字符串工具类
 * @Created by Domenic
 */
public class StringUtil {

	public static final String SPACE = " ";
	public static final String DOT = ".";
	public static final String SLASH = "/";
	public static final String BACKSLASH = "\\";
	public static final String EMPTY = "";
	public static final String CRLF = "\r\n";
	public static final String NEWLINE = "\n";
	public static final String UNDERLINE = "_";
	public static final String COMMA = ",";

	public static final String HTML_NBSP = "&nbsp;";
	public static final String HTML_AMP = "&amp";
	public static final String HTML_QUOTE = "&quot;";
	public static final String HTML_LT = "&lt;";
	public static final String HTML_GT = "&gt;";

	public static final String EMPTY_JSON = "{}";

	private StringUtil() {
		// prevents other classes from instantiating it
	}

	/**
	 * 字符串是否为空白
	 * @param str 待检测的字符串
	 * @return 是否为空白
	 */
	public static boolean isBlank(String str) {
		return str == null || str.trim().length() == 0;
	}

	/**
	 * 字符串是否为非空白
	 * @param str 待检测的字符串
	 * @return 是否为非空白
	 */
	public static boolean isNotBlank(String str) {
		return !isBlank(str);
	}

	/**
	 * 字符串是否为空
	 * @param str 待检测的字符串
	 * @return 是否为空
	 */
	public static boolean isEmpty(String str) {
		return str == null || str.length() == 0;
	}

	/**
	 * 字符串是否为非空
	 * @param str 待检测的字符串
	 * @return 是否为非空
	 */
	public static boolean isNotEmpty(String str) {
		return !isEmpty(str);
	}

	/**
	 * 判断字符串是否为空 (不能为空字符串)
	 * @param str 待检测的字符串
	 * @return {@code true} 或 {@code false}
	 */
	public static boolean isNull(String str) {
		return str == null || str.length() == 0 || str.trim().length() == 0;
	}

	/**
	 * 指定字符串是否被包装
	 * @param str 字符串
	 * @param prefix 前缀
	 * @param suffix 后缀
	 * @return 是否被包装
	 */
	public static boolean isWrap(String str, String prefix, String suffix) {
		return str.startsWith(prefix) && str.endsWith(suffix);
	}

	/**
	 * 指定字符串是否被同一字符包装 (前后都有这些字符串)
	 * @param str 字符串
	 * @param wrapper 包装字符串
	 * @return 是否被包装
	 */
	public static boolean isWrap(String str, String wrapper) {
		return isWrap(str, wrapper, wrapper);
	}

	/**
	 * 指定字符串是否被同一字符包装 (前后都有这些字符串)
	 * @param str 字符串
	 * @param wrapper 包装字符
	 * @return 是否被包装
	 */
	public static boolean isWrap(String str, char wrapper) {
		return isWrap(str, wrapper, wrapper);
	}

	/**
	 * 指定字符串是否被包装
	 * @param str 字符串
	 * @param prefixChar 前缀
	 * @param suffixChar 后缀
	 * @return 是否被包装
	 */
	public static boolean isWrap(String str, char prefixChar, char suffixChar) {
		return str.charAt(0) == prefixChar && str.charAt(str.length() - 1) == suffixChar;
	}

	/**
	 * <p>
	 * 补充字符串以满足最小长度<br/>
	 * 示例：padPre("1", 3, '0')，结果为 "001"
	 * </p>
	 * @param str 字符串
	 * @param minLength 最小长度
	 * @param padChar 补充的字符
	 * @return 补充后的字符串
	 */
	public static String padPre(String str, int minLength, char padChar) {
		if (str.length() >= minLength) {
			return str;
		}

		StringBuilder sb = new StringBuilder(minLength);
		for (int i = str.length(); i < minLength; i++) {
			sb.append(padChar);
		}
		sb.append(str);

		return sb.toString();
	}

	/**
	 * <p>
	 * * 补充字符串以满足最小长度
	 * <br/>
	 * 示例：padEnd("1", 3, '0')，结果为 "100"
	 * </p>
	 * @param str 字符串
	 * @param minLength 最小长度
	 * @param padChar 补充的字符
	 * @return 补充后的字符串
	 */
	public static String padEnd(String str, int minLength, char padChar) {
		if (str.length() >= minLength) {
			return str;
		}

		StringBuilder sb = new StringBuilder(minLength);
		sb.append(str);

		for (int i = str.length(); i < minLength; i++) {
			sb.append(padChar);
		}

		return sb.toString();
	}

	/**
	 * 创建 {@link StringBuilder} 对象
	 * @return {@link StringBuilder} 对象
	 */
	public static StringBuilder builder() {
		return new StringBuilder();
	}

	/**
	 * 创建 {@link StringBuilder} 对象，指定容量
	 * @return {@link StringBuilder} 对象
	 */
	public static StringBuilder builder(int capacity) {
		return new StringBuilder(capacity);
	}

	/**
	 * 创建 {@link StringBuilder} 对象，同时设置初始字符串
	 * @return {@link StringBuilder} 对象
	 */
	public static StringBuilder builder(String... strs) {
		final StringBuilder sb = new StringBuilder();

		for (String str : strs) {
			sb.append(str);
		}

		return sb;
	}

	/**
	 * 获得字符串对应的 {@link Byte} 数组
	 * @param str 字符串
	 * @param charset 字符集编码
	 * @return {@link Byte}[]
	 */
	public static byte[] bytes(String str, String charset) {
		if (null == str) {
			return new byte[0];
		}

		if (isBlank(charset)) {
			return new byte[0];
		}

		return str.getBytes(Charset.forName(charset));
	}

	private static final Pattern INT_PATTERN = Pattern.compile("^[+-]?\\d+$");

	/**
	 * 判断字符串是否 {@link Integer}
	 * @param input 待检测的字符串
	 * @return {@code true} 或 {@code false}
	 */
	public static boolean isInteger(String input) {
		return INT_PATTERN.matcher(input).find();
	}

	/**
	 * {@link String} 数组 转换成 {@link Integer} 数组
	 * @param str {@link String} 数组
	 * @return {@link Integer} 数组
	 */
	public static Integer[] stringArrToIntegerArr(String[] str) {
		int len = str.length;
		Integer[] intArr = new Integer[len];

		for (int i = 0; i < len; ++i) {
			intArr[i] = Integer.parseInt(str[i]);
		}

		return intArr;
	}

	/**
	 * {@link String} 数组 转换成 {@link Long} 数组
	 * @param str {@link String} 数组
	 * @return {@link Long} 数组
	 */
	public static Long[] stringArrToLongArr(String[] str) {
		int len = str.length;
		Long[] longArr = new Long[len];

		for (int i = 0; i < len; ++i) {
			longArr[i] = Long.parseLong(str[i]);
		}

		return longArr;
	}

	/**
	 * 获取文件后缀
	 * @param src 文件的路径/名称
	 * @return 文件后缀名 (不带 {@code .})
	 */
	public static String getFileExt(String src) {
		// 获取文件名
		String filename = src.substring(src.lastIndexOf(File.separator) + 1, src.length());

		return filename.substring(filename.lastIndexOf(".") + 1);
	}

	/**
	 * 获取文件后缀 (指定路径分隔符)
	 * @param src 文件的路径/名称
	 * @param separator 路径分隔符
	 * @return 文件后缀名
	 */
	public static String getFileExt(String src, String separator) {
		// 获取文件名
		String filename = src.substring(src.lastIndexOf(separator) + 1, src.length());

		return filename.substring(filename.lastIndexOf(".") + 1);
	}

	/**
	 * 获取文件名称，不带文件后缀部分
	 * @param src 文件的路径/名称
	 * @return 文件的路径/名称 (不带后缀)
	 */
	public static String getFileName(String src) {
		// 获取文件名
		String filename = src.substring(src.lastIndexOf(File.separator) + 1, src.length());

		return filename.substring(0, filename.lastIndexOf("."));
	}

	/**
	 * 获取文件名称，不带文件后缀部分
	 * @param src 文件的路径/名称
	 * @param separator 路径分隔符
	 * @return 文件的路径/名称 (不带后缀)
	 */
	public static String getFileName(String src, String separator) {
		// 获取文件名
		String filename = src.substring(src.lastIndexOf(separator) + 1, src.length());

		return filename.substring(0, filename.lastIndexOf("."));
	}

	/**
	 * 检查 {@link String} 数组中，是否含有指定字符串
	 * @param strArr 带检测的 {@link String} 数组
	 * @param valueToCheck 要查找的元素
	 * @return {@code true} 或 {@code false}
	 */
	public static Boolean checkArrayValue(String[] strArr, String valueToCheck) {
		Boolean checkFlag = false;

		if (strArr != null && strArr.length > 0) {
			for (String str : strArr) {
				if (str.equals(valueToCheck)) {
					checkFlag = true;
					break;
				}
			}
		}

		return checkFlag;
	}

	/**
	 * 检查 {@link String} 数组中的元素，是否在指定字符串中出现
	 * @param arr
	 * @param checkValue
	 * @return
	 */
	public static Boolean isContains(String[] strArr, String checkValue) {
		Boolean checkFlag = false;

		if (strArr != null && strArr.length > 0) {
			for (String str : strArr) {
				// 只要在指定字符串中出现，就返回true
				if (checkValue.indexOf(str) != -1) {
					checkFlag = true;
					break;
				}
			}
		}

		return checkFlag;
	}

	/**
	 * 生成 UUID (32 位 16 进制数)
	 * @return {@link UUID} 字符串
	 */
	public static String getUuid() {
		return UUID.randomUUID().toString().replace("-", "");
	}

}
