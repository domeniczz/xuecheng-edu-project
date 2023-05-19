package com.xuecheng.base.config;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import lombok.extern.slf4j.Slf4j;

/**
 * @author Domenic
 * @Classname CustomPropertiesReader
 * @Description 读取自定义的配置文件
 * @Created by Domenic
 */
@Slf4j
public class CustomPropertiesReader {

    private CustomPropertiesReader() {
    }

    /**
     * 读取文件的最大大小
     */
    public static final int MAX_FILE_READ_SIZE;

    public static final String FFMPEG_PATH;

    public static final int MAX_RETRY_COUNT;

    private static Properties properties = new Properties();

    static {
        String propertiesFile = "custom-config.properties";

        try (InputStream is = CustomPropertiesReader.class.getClassLoader().getResourceAsStream(propertiesFile)) {
            if (is == null) {
                log.error("Unable to find \"" + propertiesFile + "\"!");
            }

            // Load the configuration file
            properties.load(is);

        } catch (IOException e) {
            e.printStackTrace();
            throw new IllegalArgumentException("Error loading properties file, maybe it does not exist", e);
        }

        // 从配置文件中获取值
        MAX_FILE_READ_SIZE = Integer.valueOf(properties.getProperty("file.read.maxsize"));
        FFMPEG_PATH = properties.getProperty("ffmpeg.path");
        MAX_RETRY_COUNT = Integer.valueOf(properties.getProperty("video.process.maxretry"));
    }

}
