package com.xuecheng.media.service.jobhandler;

import com.xuecheng.base.utils.FileUtil;
import com.xuecheng.base.utils.Mp4VideoUtil;
import com.xuecheng.base.utils.StringUtil;
import com.xuecheng.media.model.po.MediaProcess;
import com.xuecheng.media.operations.MinioOperation;
import com.xuecheng.media.service.MediaProcessService;
import com.xxl.job.core.context.XxlJobHelper;
import com.xxl.job.core.handler.annotation.XxlJob;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import io.minio.ObjectWriteResponse;
import lombok.extern.slf4j.Slf4j;

/**
 * @author Domenic
 * @Classname VideoProcessingJob
 * @Description 视频处理任务类
 * @Created by Domenic
 */
@Component
@Slf4j
public class VideoProcessingJob {

    @Autowired
    private MediaProcessService mediaProcessService;

    @Autowired
    private MinioOperation minioOperation;

    @Value("${media.processing.maxfailcount}")
    private int maxWaitingMinutes;

    /**
     * 视频处理任务
     */
    @XxlJob("videoProcessingJobHandler")
    public void videoProcessingJobHandler() throws InterruptedException {

        List<MediaProcess> taskList = getTaskList();

        ThreadPoolExecutor executor = getThreadPool(taskList.size());

        CountDownLatch countDownLatch = new CountDownLatch(taskList.size());

        if (executor != null) {
            taskList.forEach(mediaProcess -> executor.execute(() -> {
                processingVideo(mediaProcess);
                countDownLatch.countDown();
            }));
        }

        boolean awaitRes = countDownLatch.await(maxWaitingMinutes, TimeUnit.MINUTES);
        if (!awaitRes) {
            log.error("视频处理任务执行超时 (共应该执行 {} 个任务)", taskList.size());
        }

    }

    /**
     * 处理视频文件
     * @param mediaProcess 视频处理任务
     */
    private void processingVideo(MediaProcess mediaProcess) {
        boolean startRes = mediaProcessService.startTask(mediaProcess);

        if (startRes) {
            log.debug("视频处理任务 {} 开始执行, 处理视频文件 {}", mediaProcess.getId(), mediaProcess.getFilename());

            ObjectWriteResponse resp = transcodeVideoFile(mediaProcess);

            if (resp == null) {
                log.error("视频处理任务 {} 执行失败, 处理视频文件 {}", mediaProcess.getId(), mediaProcess.getFilename());
                mediaProcessService.handleFailedTask(mediaProcess);
                return;
            }

            String bucket = resp.bucket();
            String objectName = resp.object();

            deleteOriginalVideoFile(bucket, objectName);

            String url = bucket + "/" + objectName;
            log.debug("视频处理任务 {} 执行成功, 处理视频文件 {}, 处理后文件访问地址 {}", mediaProcess.getId(), mediaProcess.getFilename(), url);

            mediaProcess.setUrl(url);
            mediaProcessService.handleSucceededTask(mediaProcess);
        }
    }

    /**
     * 获取文件，对视频文件进行转码，再将文件上传到文件服务器
     * @param originalFile 原始文件
     */
    private ObjectWriteResponse transcodeVideoFile(MediaProcess mediaProcess) {
        Path originalFile = minioOperation.downloadFile(mediaProcess.getBucket(), mediaProcess.getFilePath());

        Path outputFile = null;
        try {
            outputFile = FileUtil.createTempFileIfNotExist("transcode-" + StringUtil.getUuid(), "mp4");
        } catch (IOException e) {
            log.error("创建临时文件失败, errorMsg={}", e.getMessage());
            return null;
        }

        if (outputFile != null) {
            Optional<String> res = Mp4VideoUtil.generateMp4(originalFile.toAbsolutePath().toString(), outputFile.toAbsolutePath().toString());
            if (res.isPresent()) {
                log.error("视频转码可能出错, bucket={}, filePath={}, output={}", mediaProcess.getBucket(), mediaProcess.getFilePath(), res.get());
                return null;
            }
        }

        if (outputFile != null) {
            return minioOperation.uploadFile(
                    outputFile.toAbsolutePath().toString(),
                    FileUtil.getMimeTypeFromExt("mp4"),
                    mediaProcess.getBucket(),
                    mediaProcess.getFilePath());
        }

        return null;
    }

    /**
     * 删除原始 (未转码) 的视频文件
     * @param bucket 桶名
     * @param objectName 对象名 (文件在 minio 中的路径)
     */
    private void deleteOriginalVideoFile(String bucket, String objectName) {
        boolean res = minioOperation.deleteFile(bucket, objectName);
        if (!res) {
            log.error("删除原始视频文件失败, bucket={}, objectName={}", bucket, objectName);
        }
    }

    /**
     * 获取待执行任务列表
     * @return 任务列表 {@link List}&lt;{@link MediaProcess}&gt;
     */
    private List<MediaProcess> getTaskList() {
        int shardIndex = XxlJobHelper.getShardIndex();
        int shardTotal = XxlJobHelper.getShardTotal();

        int processorCount = Runtime.getRuntime().availableProcessors();

        return mediaProcessService.getTaskListForShard(shardIndex, shardTotal, processorCount);
    }

    /**
     * 根据任务数, 获取线程池执行器
     * @param taskNum 任务数
     * @return 线程池 {@link ThreadPoolExecutor}
     */
    private ThreadPoolExecutor getThreadPool(int taskNum) {
        if (taskNum <= 0) {
            log.debug("无待执行的视频处理任务");
            return null;
        }

        log.debug("待执行的视频处理任务数: " + taskNum);

        int corePoolSize = 0;
        int maximumPoolSize = taskNum;
        long keepAliveTime = 60L;
        BlockingQueue<Runnable> pendingTasksQueue = new LinkedBlockingQueue<>(taskNum);

        return new ThreadPoolExecutor(
                corePoolSize,
                maximumPoolSize,
                keepAliveTime,
                TimeUnit.SECONDS,
                pendingTasksQueue,
                new ThreadPoolExecutor.AbortPolicy());
    }

}
