package com.xuecheng.content.config;

import com.baomidou.mybatisplus.annotation.DbType;
import com.baomidou.mybatisplus.extension.plugins.MybatisPlusInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.PaginationInnerInterceptor;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author Domenic
 * @Classname MybatisPlusConfig
 * @Description Mybatis Plus 配置类
 * @Created by Domenic
 */
@Configuration
@MapperScan("com.xuecheng.content.mapper")
public class MybatisPlusConfig {

    /**
     * 定义分页拦截器
     * 拦截器会拦截待执行的 SQL 语句，并根据存放在 ThreadLocal 中的分页参数，进行 SQL 的重写
     */
    @Bean
    public MybatisPlusInterceptor mybatisPlusInterceptor() {
        MybatisPlusInterceptor interceptor = new MybatisPlusInterceptor();
        interceptor.addInnerInterceptor(new PaginationInnerInterceptor(DbType.MYSQL));
        return interceptor;
    }

    // @Bean
    // public ConfigurationCustomizer configurationCustomizer() {
    //     return configuration -> {
    //         TypeAliasRegistry typeAliasRegistry = configuration.getTypeAliasRegistry();
    //         typeAliasRegistry.registerAliases("com.xuecheng.content.model.po");
    //         typeAliasRegistry.registerAliases("com.xuecheng.content.model.dto");
    //     };
    // }

}
