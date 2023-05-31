package com.xuecheng.media.service;

import com.xuecheng.media.operations.MinioOperation;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.IOException;
import java.security.GeneralSecurityException;

import io.minio.errors.MinioException;

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
    void testClearResidualChunkFiles() throws IOException, MinioException, GeneralSecurityException {
        boolean res = minioOperation.clearResidualChunkFiles(bucket);
        Assertions.assertTrue(res, "清除残留分片文件出错");
    }

}
