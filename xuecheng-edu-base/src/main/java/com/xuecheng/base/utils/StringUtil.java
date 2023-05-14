package com.xuecheng.base.utils;

import java.util.UUID;

/**
 * @author Domenic
 * @Classname StringUtil
 * @Description 字符串工具类
 * @Created by Domenic
 */
public class StringUtil {

	/**
	 * 生成 UUID (32 位 16 进制数)
	 * @return {@link UUID} 字符串
	 */
	public static String getUuid() {
		return UUID.randomUUID().toString().replace("-", "");
	}

}
