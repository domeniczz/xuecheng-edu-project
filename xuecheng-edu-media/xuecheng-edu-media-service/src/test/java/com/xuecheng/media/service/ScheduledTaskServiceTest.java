package com.xuecheng.media.service;

import com.xuecheng.media.utils.MinioUtils;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class ScheduledTaskServiceTest {

    @Autowired
    private MinioUtils minioUtils;

    /**
     * 存储视频的桶
     */
    @Value("${minio.bucket.videofiles}")
    private String bucket;

    @Test
    void test_clearResidualChunkFiles() {
        minioUtils.clearResidualChunkFiles(bucket);
    }

}
