package com.xuecheng;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.PropertySource;

/**
 * @author Domenic
 * @Classname ContentApplication
 * @Description 媒资管理服务测试的启动类
 * @Created by Domenic
 */
@SpringBootApplication
@PropertySource("classpath:test-config.properties")
public class TestApplication {

    public static void main(String[] args) {
        SpringApplication.run(TestApplication.class, args);
    }

}
