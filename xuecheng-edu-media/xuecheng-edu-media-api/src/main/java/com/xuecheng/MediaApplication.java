package com.xuecheng;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import com.spring4all.swagger.EnableSwagger2Doc;

/**
 * @author Domenic
 * @Classname MediaApplication
 * @Description 媒资管理启动类
 * @Created by Domenic
 */
@SpringBootApplication
@EnableSwagger2Doc
public class MediaApplication {

	public static void main(String[] args) {
		SpringApplication.run(MediaApplication.class, args);
	}

}