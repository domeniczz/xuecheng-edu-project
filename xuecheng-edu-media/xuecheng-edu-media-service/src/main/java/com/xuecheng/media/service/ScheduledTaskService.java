package com.xuecheng.media.service;

/**
 * @author Domenic
 * @Classname ScheduledTaskService
 * @Description 定时任务接口
 * @Created by Domenic
 */
public interface ScheduledTaskService {

    /**
     * 清理所有残留的分块文件
     */
    void clearResidualChunkFiles();

}
