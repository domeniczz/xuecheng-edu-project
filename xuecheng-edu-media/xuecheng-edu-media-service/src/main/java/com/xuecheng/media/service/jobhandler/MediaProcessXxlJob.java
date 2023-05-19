package com.xuecheng.media.service.jobhandler;

import com.xuecheng.media.utils.MinioUtils;
import com.xxl.job.core.context.XxlJobHelper;
import com.xxl.job.core.handler.annotation.XxlJob;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * @author Domenic
 * @Classname SampleXxlJob
 * @Description 媒体文件处理任务类
 * @Created by Domenic
 */
@Component
public class MediaProcessXxlJob {

    @Autowired
    private MinioUtils minioUtils;

    /**
     * 存储视频的桶
     */
    @Value("${minio.bucket.videofiles}")
    private String bucket;

    /**
     * 清理 Minio 中残留分块文件的任务
     * @throws Exception
     */
    @XxlJob("residualFileCleanerJob")
    public void residualFileCleanerJob() {
        minioUtils.clearResidualChunkFiles(bucket);
    }

    /**
     * 示例分片广播任务，将任务发送给所有的执行器
     * <!-- 本地测试时，启动多个实例 -->
     * @throws Exception
     */
    @XxlJob("sampleSharingJobHandler")
    public void sampleSharingJobHandler() {

        org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(MediaProcessXxlJob.class);

        int shardIndex = XxlJobHelper.getShardIndex();
        int shardTotal = XxlJobHelper.getShardTotal();

        log.debug("当前分片索引: {}, 当前分片总数: {}", shardIndex, shardTotal);
    }

}
