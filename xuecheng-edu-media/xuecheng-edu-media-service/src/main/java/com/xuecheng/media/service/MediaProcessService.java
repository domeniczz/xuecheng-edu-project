package com.xuecheng.media.service;

import com.xuecheng.media.model.po.MediaFile;
import com.xuecheng.media.model.po.MediaProcess;

import java.util.List;
import java.util.Optional;

/**
 * @author Domenic
 * @Classname MediaProcessService
 * @Description 文件处理服务接口
 * @Created by Domenic
 */
public interface MediaProcessService {

    /**
     * 获取待处理任务列表
     * @param shardIndex 分片序号
     * @param shardTotal 分片总数
     * @param taskCount 获取的任务数
     * @return 待处理任务列表 {@link List}&lt;{@link MediaProcess}&gt;
     */
    List<MediaProcess> getTaskListForShard(int shardIndex, int shardTotal, int taskCount);

    /**
     * 添加待处理任务到数据库中
     * @param mediaFile 待处理的文件的信息 {@link MediaFile}
     * @return {@code true} 添加成功, {@code false} 添加失败
     */
    boolean addPendingTask(MediaFile mediaFile);

    /**
     * <p>
     * 执行待处理任务<br/>
     * 若有多个线程, 可能存在多个线程同时尝试执行同一个任务的情况, 但是只有一个线程能够成功执行<br/>
     * 该方法会判断当前任务是否有线程在执行: 若无, 则当前线程执行该任务; 若有, 则不执行
     * </p>
     * @param tasks 待处理任务列表
     * @return {@code true} or {@code false}
     */
    boolean startTask(MediaProcess tasks);

    /**
     * <p>
     * 处理执行成功的任务<br/>
     * 处理成功的任务, 到已完成的任务列表中
     * </p>
     * @param task 成功的任务
     * @return {@code true} 处理成功, {@code Optional.empty()} 处理失败
     */
    Optional<Boolean> handleSucceededTask(MediaProcess task);

    /**
     * <p>
     * 处理执行失败的任务<br/>
     * 若重试次数超过 3 次, 则处理失败的任务, 到已完成的任务列表中
     * </p>
     * @param task 失败的任务
     * @return {@code true} 处理成功, {@code Optional.empty()} 处理失败
     */
    Optional<Boolean> handleFailedTask(MediaProcess task);

}
