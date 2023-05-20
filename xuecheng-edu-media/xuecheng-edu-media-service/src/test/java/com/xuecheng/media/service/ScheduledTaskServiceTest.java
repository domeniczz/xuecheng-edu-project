package com.xuecheng.media.service;

import com.xuecheng.media.operations.MinioOperation;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class ScheduledTaskServiceTest {

    @Autowired
    private MinioOperation minioOperation;

    /**
     * 存储视频的桶
     */
    @Value("${minio.bucket.videofiles}")
    private String bucket;

    @Test
    void testClearResidualChunkFiles() {
        minioOperation.clearResidualChunkFiles(bucket);
    }

}
