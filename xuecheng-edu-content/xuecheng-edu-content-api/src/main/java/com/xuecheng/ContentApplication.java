package com.xuecheng;

import com.spring4all.swagger.EnableSwagger2Doc;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * @author Domenic
 * @Classname ContentApplication
 * @Description 内容管理服务的启动类
 * @Created by Domenic
 */
@SpringBootApplication
@EnableSwagger2Doc
public class ContentApplication {

    public static void main(String[] args) {
        SpringApplication.run(ContentApplication.class, args);
    }

}
