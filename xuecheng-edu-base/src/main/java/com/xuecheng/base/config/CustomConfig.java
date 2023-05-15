package com.xuecheng.base.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

/**
 * @author Domenic
 * @Classname CustomConfig
 * @Description 自定义配置
 * @Created by Domenic
 */
@Configuration
@PropertySource("classpath:custom-config.properties")
public class CustomConfig {

}
