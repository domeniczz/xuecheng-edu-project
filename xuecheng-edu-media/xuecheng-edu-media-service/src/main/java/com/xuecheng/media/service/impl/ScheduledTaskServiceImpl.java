package com.xuecheng.media.service.impl;

import com.xuecheng.media.service.ScheduledTaskService;
import com.xuecheng.media.utils.MinioUtils;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

/**
 * @author Domenic
 * @Classname ScheduledTaskServiceImpl
 * @Description 定时任务接口实现类
 * @Created by Domenic
 */
@Service
public class ScheduledTaskServiceImpl implements ScheduledTaskService {

    @Autowired
    private MinioUtils minioUtils;

    /**
     * 存储视频的桶
     */
    @Value("${minio.bucket.videofiles}")
    private String bucket;

    @Override
    public void clearResidualChunkFiles() {
        minioUtils.clearResidualChunkFiles(bucket);
    }

}
