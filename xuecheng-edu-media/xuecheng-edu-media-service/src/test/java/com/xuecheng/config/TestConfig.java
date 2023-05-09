package com.xuecheng.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

/**
 * @author Domenic
 * @Classname TestConfig
 * @Description 测试应用配置
 * @Created by Domenic
 */
@Configuration
@PropertySource("classpath:test-config.properties")
public class TestConfig {

}
