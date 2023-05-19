package com.xuecheng.base.utils;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * @author Domenic
 * @Classname IDCardUtil
 * @Description 身份证号工具类
 * @Created by Domenic
 */
public class IDCardUtil {

	/**
	 * 一代身份证号的长度
	 */
	private static final int ID_LENGTH_GEN1 = 15;

	/**
	 * 二代身份证号的长度
	 */
	private static final int ID_LENGTH_GEN2 = 18;

	private IDCardUtil() {
		// prevents other classes from instantiating it
	}

	/**
	 * 通过身份证号码获取 生日 (birthday), 年龄 (), 性别 (gender)
	 * 返回出生日期 yyyy-MM-dd; 性别 M-男, F-女
	 * @param idNumber 身份证号
	 * @return {@link Map}&lt;{@link String}, {@link String}&gt;
	 */
	public static Map<String, String> getInfo(String idNumber) {
		String birthday = "";
		String age = "";
		String gender = "";

		// 获取当前年份
		int year = LocalDate.now().getYear();

		// 若是一代身份证
		if (idNumber.length() == ID_LENGTH_GEN1) {
			birthday = "19" + idNumber.substring(6, 8) + "-" + idNumber.substring(8, 10) + "-" + idNumber.substring(10, 12);
			gender = Integer.parseInt(idNumber.substring(idNumber.length() - 3, idNumber.length())) % 2 == 0 ? "F" : "M";
			age = String.valueOf(year - Integer.parseInt("19" + idNumber.substring(6, 8)));
		}
		// 若是二代身份证
		else if (idNumber.length() == ID_LENGTH_GEN2) {
			birthday = idNumber.substring(6, 10) + "-" + idNumber.substring(10, 12) + "-" + idNumber.substring(12, 14);
			gender = Integer.parseInt(idNumber.substring(idNumber.length() - 4, idNumber.length() - 1)) % 2 == 0 ? "F" : "M";
			age = String.valueOf(year - Integer.parseInt(idNumber.substring(6, 10)));
		}

		// Calculate age using LocalDate for better accuracy
		Optional<LocalDate> birthdate = Optional.ofNullable(birthday)
				.filter(b -> !b.isEmpty())
				.map(b -> LocalDate.parse(b, DateTimeFormatter.ofPattern("yyyy-MM-dd")));
		if (birthdate.isPresent()) {
			age = String.valueOf(ChronoUnit.YEARS.between(birthdate.get(), LocalDate.now()));
		}

		Map<String, String> map = new HashMap<>(3);
		map.put("birthday", birthday);
		map.put("age", age);
		map.put("gender", gender);

		return map;
	}

}
