package com.xuecheng.base.utils;

import org.apache.commons.lang.math.NumberUtils;
import org.apache.commons.lang3.StringUtils;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.StringTokenizer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Domenic
 * @Classname CommonUtil
 * @Description 通用工具类
 * @Created by Domenic
 */
public class CommonUtil {

	private CommonUtil() {
		// prevents other classes from instantiating it
	}

	/**
	 * If `mobile` is empty or {@code null}, returns an empty {@link String}.<br/>
	 * Otherwise, this method replaces the middle 4 digits of the mobile number with {@code *},
	 * and returns the modified mobile string.
	 * @param mobile Mobile number (11 digits)
	 * @return Modified mobile string
	 */
	public static String hiddenMobile(String mobile) {
		if (StringUtils.isBlank(mobile)) {
			return "";
		}
		// Divide the mobile number into three parts: 3 + 4 + 4.
		// Replace the middle 4 digits with asterisks (*)
		return mobile.replaceAll("(\\d{3})\\d{4}(\\d{4})", "$1****$2");
	}

	/**
	 * Converts a {@link String} to a {@link BigDecimal} object
	 * @param s the string to convert
	 * @param isFen whether the string represents fen (a smaller unit in some currencies)
	 * @return the BigDecimal object
	 */
	public static BigDecimal strToBigDecimal(String s, Boolean isFen) {
		if (StringUtils.isBlank(s)) {
			return null;
		}

		if (!NumberUtils.isNumber(s)) {
			return null;
		}

		BigDecimal decimal = new BigDecimal(s);
		if (isFen != null && isFen.booleanValue()) {
			decimal = decimal.divide(new BigDecimal(100), 2);
		}
		return decimal;
	}

	/**
	 * <p>
	 * Matches time strings in the format of "HH:mm:ss", "HH:mm", "mm:ss", "ss"<br/>
	 * Supports both Chinese and English colons
	 * </p>
	 */
	public static final Pattern HOUR_MIN_SEC_PATTERN = Pattern.compile("(\\d{1,2}[：:]){0,2}\\d{1,2}");

	/**
	 * Convert "hours & minutes & seconds" to seconds
	 * @param hourMinSec String of "hours & minutes & seconds"
	 * @return Seconds
	 */
	public static Long hourMinSecToSeconds(String hourMinSec) {
		if (StringUtils.isBlank(hourMinSec)) {
			return 0L;
		}

		Long totalSeconds = 0L;
		hourMinSec = hourMinSec.replace(" ", "");
		boolean matched = HOUR_MIN_SEC_PATTERN.matcher(hourMinSec).matches();

		if (matched) {
			List<String> sfmList = new ArrayList<>();
			StringTokenizer st = new StringTokenizer(hourMinSec, "：:");
			while (st.hasMoreTokens()) {
				sfmList.add(st.nextToken());
			}
			Collections.reverse(sfmList);
			String[] sfmArr = sfmList.toArray(new String[0]);
			for (int i = 0; i < sfmArr.length; i++) {
				if (i == 0) {
					totalSeconds += Long.valueOf(sfmArr[i]);
				} else if (i == 1) {
					totalSeconds += Long.valueOf(sfmArr[i]) * 60;
				} else if (i == 2) {
					totalSeconds += Long.valueOf(sfmArr[i]) * 3600;
				}
			}
		}

		return totalSeconds;
	}

	/**
	 * Regular expression used to map Underscore to CamelCase naming
	 * Precompile the regex for efficiency
	 */
	private static final Pattern UTC_PATTERN = Pattern.compile("_([a-z])");

	/**
	 * Map Underscore to CamelCase naming
	 * @param str String to be converted (Underscore)
	 * @return Converted string (CamelCase)
	 */
	public static String mapUnderscoreToCamelCase(String str) {
		// 先转成全小写
		str = str.toLowerCase();
		final Matcher matcher = UTC_PATTERN.matcher(str);
		while (matcher.find()) {
			str = str.replaceAll(matcher.group(), matcher.group(1).toUpperCase());
		}
		return str;
	}

	/**
	 * Map CamelCase naming to Underscore
	 * ATTENTION: Input must be in standard camel case format, otherwise strange results may occur
	 * @param str String to be converted (CamelCase)
	 * @return Converted string (Underscore)
	 */
	public static String mapCamelCaseToUnderscore(String str) {
		return str.replaceAll("([A-Z])", "_$1").toUpperCase();
	}

}
