package com.xuecheng.system;

import com.spring4all.swagger.EnableSwagger2Doc;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * @author Domenic
 * @Classname SystemApplication
 * @Description 系统管理启动类
 * @Created by Domenic
 */
@EnableScheduling
@EnableSwagger2Doc
@SpringBootApplication
public class SystemApplication {

    public static void main(String[] args) {
        SpringApplication.run(SystemApplication.class, args);
    }

}