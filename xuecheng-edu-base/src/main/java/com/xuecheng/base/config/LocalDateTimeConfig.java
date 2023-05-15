package com.xuecheng.base.config;

import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.jackson.Jackson2ObjectMapperBuilderCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * @author Domenic
 * @Classname LocalDateTimeConfig
 * @Description Set Date Time Format
 * @Created by Domenic
 */
@Configuration
public class LocalDateTimeConfig {

    @Value("${datetime.pattern}")
    private String dateTimePattern;

    @Value("${date.pattern}")
    private String datePattern;

    /**
     * <p>
     * 使用指定格式化模式，来序列化  {@link LocalDateTime}<br/>
     * 发出内容时，将日期序列化为字符串 (LocalDateTime -> String)
     * </p>
     */
    @Bean
    public LocalDateTimeSerializer localDateTimeSerializer() {
        return new LocalDateTimeSerializer(DateTimeFormatter.ofPattern(dateTimePattern));
    }

    /**
     * <p>
     * 使用指定格式化模式，来反序列化 {@link LocalDateTime}<br/>
     * 接收内容时，将字符串反序列化为日期 (String -> LocalDateTime)
     * </p>
     */
    @Bean
    public LocalDateTimeDeserializer localDateTimeDeserializer() {
        return new LocalDateTimeDeserializer(DateTimeFormatter.ofPattern(dateTimePattern));
    }

    /**
     * <p>
     * This bean is a customization hook provided by Spring Boot<br/>
     * to modify the {@code ObjectMapper} instances used by the application
     * </p>
     * <p>
     * In this case, it's used to register custom serializer and deserializer for {@link LocalDateTime} instances
     * </p>
     * @return
     */
    @Bean
    public Jackson2ObjectMapperBuilderCustomizer jackson2ObjectMapperBuilderCustomizer() {
        return builder -> {
            builder.serializerByType(LocalDateTime.class, localDateTimeSerializer());
            builder.deserializerByType(LocalDateTime.class, localDateTimeDeserializer());
        };
    }

    @Bean("dateTimeFormatter")
    public DateTimeFormatter dateTimeFormatter() {
        return DateTimeFormatter.ofPattern(dateTimePattern);
    }

    @Bean("dateFormatter")
    public DateTimeFormatter dateFormatter() {
        return DateTimeFormatter.ofPattern(datePattern);
    }

}
