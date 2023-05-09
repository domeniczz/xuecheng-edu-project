package com.xuecheng.media.tasks;

import com.xuecheng.media.service.ScheduledTaskService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

import lombok.extern.slf4j.Slf4j;

/**
 * @author Domenic
 * @Classname DailyTaskScheduler
 * @Description 定时任务
 * @Created by Domenic
 */
@Component
@Slf4j
public class TaskScheduler {

    @Autowired
    private ScheduledTaskService scheduledTaskService;

    /* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
     * Cron Expression Explaination:                                           *
     * - Seconds (0-59)                                                        *
     * - Minutes (0-59)                                                        *
     * - Hours (0-23)                                                          *
     * - Day of the month (1-31)                                               *
     * - Month (1-12 or JAN-DEC)                                               *
     * - Day of the week (0-7 or SUN-SAT, where both 0 and 7 represent Sunday) *
     *                                                                         *
     * Symbols:                                                                *
     * - '*' means "any" or "all" possible values for a specific field         *
     * - '?' means no specific value for this field                            *
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

    /**
     * 定时清理 minio 文件 (每周日 22:00 执行)
     */
    @Scheduled(cron = "0 0 22 ? * SUN")
    public void scheduledResidualFileCleaner() {
        log.debug("MinIO 文件清理任务开始执行 - " + LocalDateTime.now());

        scheduledTaskService.clearResidualChunkFiles();

        log.debug("MinIO 文件清理任务执行完毕 - " + LocalDateTime.now());
    }
}
