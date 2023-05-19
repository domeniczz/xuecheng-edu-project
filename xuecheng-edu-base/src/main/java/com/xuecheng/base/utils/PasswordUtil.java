package com.xuecheng.base.utils;

import java.security.SecureRandom;

/**
 * @author Domenic
 * @Classname PasswordUtil
 * @Description 密码工具类
 * @Created by Domenic
 */
public class PasswordUtil {

	private PasswordUtil() {
		// prevents other classes from instantiating it
	}

	/**
	 * Generate a salted password
	 * @param password original password
	 * @return salted password
	 */
	public static String generate(String password) {
		if (StringUtil.isEmpty(password)) {
			throw new IllegalArgumentException("Password cannot be null or empty");
		}

		/* Generate a random 16-character string (salt) */
		int capacity = 16;

		SecureRandom random = new SecureRandom();
		StringBuilder builder = new StringBuilder(capacity);
		builder.append(random.nextInt(99999999)).append(random.nextInt(99999999));

		// Ensures that the StringBuilder is at least capacity characters long.
		// If the current string is shorter than capacity, it's padded with null characters.
		builder.setLength(capacity);
		// replaces the null characters with zeros
		String salt = builder.toString().replace('\0', '0');

		/* Hash the password with salt */
		password = Md5Util.get32Md5(password + salt);

		/*
		 * Combine the hashed password and salt in a specific manner
		 * 
		 * It iterates (in steps of 3) through a 48-character array.
		 * At each step, it takes two characters from the hashed password and one character from the salt,
		 * and appends them in this order to a char array.
		 * It generates a 48-character string consisting of the hashed password and the salt interlaced together.
		 */
		int len = 48;
		int step = 3;
		char[] cs = new char[len];

		for (int i = 0; i < len; i += step) {
			cs[i] = password.charAt(i / 3 * 2);
			char c = salt.charAt(i / 3);
			cs[i + 1] = c;
			cs[i + 2] = password.charAt(i / 3 * 2 + 1);
		}

		return new String(cs);
	}

	/**
	 * Check if the password is correct
	 * @param password input password
	 * @param md5 hashed salted password
	 * @return {@code true} or {@code false}
	 */
	public static boolean verify(String password, String md5) {
		if (StringUtil.isEmpty(password)) {
			throw new IllegalArgumentException("Password cannot be null or empty");
		}
		if (StringUtil.isEmpty(md5)) {
			throw new IllegalArgumentException("MD5 value cannot be null or empty");
		}

		char[] cs1 = new char[32];
		char[] cs2 = new char[16];

		int len = 48;
		int step = 3;

		for (int i = 0; i < len; i += step) {
			cs1[i / 3 * 2] = md5.charAt(i);
			cs1[i / 3 * 2 + 1] = md5.charAt(i + 2);
			cs2[i / 3] = md5.charAt(i + 1);
		}
		String salt = new String(cs2);

		return Md5Util.get32Md5(password + salt).equals(new String(cs1));
	}

}