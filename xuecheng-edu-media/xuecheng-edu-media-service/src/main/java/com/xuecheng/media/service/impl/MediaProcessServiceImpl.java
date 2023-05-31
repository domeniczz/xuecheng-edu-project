package com.xuecheng.media.service.impl;

import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.xuecheng.base.exception.OperationFailedException;
import com.xuecheng.base.utils.FileUtil;
import com.xuecheng.media.mapper.MediaFileMapper;
import com.xuecheng.media.mapper.MediaProcessHistoryMapper;
import com.xuecheng.media.mapper.MediaProcessMapper;
import com.xuecheng.media.model.po.MediaFile;
import com.xuecheng.media.model.po.MediaProcess;
import com.xuecheng.media.service.MediaProcessService;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import lombok.extern.slf4j.Slf4j;

/**
 * @author Domenic
 * @Classname MediaProcessServiceImpl
 * @Description 文件处理服务接口实现类
 * @Created by Domenic
 */
@Service
@Slf4j
public class MediaProcessServiceImpl implements MediaProcessService {

    @Autowired
    private MediaProcessMapper mediaProcessMapper;

    @Autowired
    private MediaProcessHistoryMapper mediaProcessHistoryMapper;

    @Autowired
    private MediaFileMapper mediaFileMapper;

    @Value("${media.processing.maxfailcount}")
    private int maxFailCount;

    private static Map<String, String> videoTypes = new HashMap<>();

    static {
        videoTypes.put("avi", "video/x-msvideo");
    }

    @Override
    public List<MediaProcess> getTaskListForShard(int shardIndex, int shardTotal, int taskCount) {
        return mediaProcessMapper.selectMediaProcessList(shardIndex, shardTotal, taskCount);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean addPendingTask(MediaFile mediaFile) {

        String filename = mediaFile.getFilename();
        String mimeType = FileUtil.getMimeType(filename);

        String acceptedVideoType = "avi";

        if (!videoTypes.get(acceptedVideoType).equals(mimeType)) {
            log.debug("视频文件 {} 不是 {} 格式, 不添加入待处理文件数据库", filename, acceptedVideoType);
            return false;
        }

        MediaProcess mediaProcess = new MediaProcess();

        BeanUtils.copyProperties(mediaFile, mediaProcess);

        // 状态 1 表示未处理
        mediaProcess.setStatus("1");
        // 失败次数默认为 0
        mediaProcess.setFailCount(0);
        mediaProcess.setCreateDate(LocalDateTime.now());
        // 先设置为 null，视频转码成功后，再设置为转码后的视频访问地址
        mediaProcess.setUrl(null);

        int res = mediaProcessMapper.insert(mediaProcess);
        return res > 0;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean startTask(MediaProcess task) {
        String taskFinishedSuccessfully = "2";

        if (!taskFinishedSuccessfully.equals(task.getStatus())) {
            return mediaProcessMapper.startProcessOnce(task.getId()) == 1;
        }

        return false;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Optional<Boolean> handleSucceededTask(MediaProcess task) {
        String taskName = "文件处理成功后, 转储信息";

        if (task.getUrl() != null && task.getUrl().isEmpty()) {
            log.error("任务 (ID: {}) 成功, 但处理后的文件的访问 URL 为空", task.getId());
            throw new OperationFailedException(taskName, new Throwable("处理后的文件的访问 URL 为空"));
        }

        String taskFinishedSuccessfully = "2";

        MediaProcess taskRes = mediaProcessMapper.selectById(task.getId());
        taskRes.setStatus(taskFinishedSuccessfully);
        taskRes.setFinishDate(LocalDateTime.now());
        taskRes.setUrl(task.getUrl());

        MediaFile mediaFile = new MediaFile();
        mediaFile.setUrl(task.getUrl());
        if (mediaFileMapper.update(mediaFile, new LambdaUpdateWrapper<MediaFile>()
                .eq(MediaFile::getId, task.getFileId())
                .set(MediaFile::getUrl, task.getUrl())) != 1) {
            log.error(taskName + "失败, 更新文件信息表中的访问 URL 失败", task.getId());
            throw new OperationFailedException(taskName, new Throwable("更新文件信息表中的访问 URL 失败"));
        }

        if (mediaProcessHistoryMapper.insert(taskRes) != 1) {
            log.error(taskName + "失败, 将完成的文件处理任务, 插入文件处理历史表失败", task.getId());
            throw new OperationFailedException(taskName, new Throwable("将完成的文件处理任务, 插入文件处理历史表失败"));
        }

        if (mediaProcessMapper.deleteById(task.getId()) != 1) {
            log.error(taskName + "失败, 删除该文件的待处理任务失败", task.getId());
            throw new OperationFailedException(taskName, new Throwable("删除该文件的待处理任务失败"));
        }

        return Optional.of(true);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Optional<Boolean> handleFailedTask(MediaProcess task) {
        if (task.getFailCount() < maxFailCount) {
            return Optional.empty();
        }

        String taskName = "文件处理失败后, 转储信息";
        String taskFailedStatus = "3";

        MediaProcess failedMediaProcess = mediaProcessMapper.selectById(task.getId());
        failedMediaProcess.setStatus(taskFailedStatus);
        failedMediaProcess.setFinishDate(LocalDateTime.now());
        failedMediaProcess.setFailCount(task.getFailCount());
        failedMediaProcess.setUrl(null);

        if (mediaProcessMapper.update(failedMediaProcess, new LambdaUpdateWrapper<MediaProcess>()
                .eq(MediaProcess::getFileId, task.getFileId())) != 1) {
            log.error(taskName + "失败, 将失败的文件处理任务, 插入文件处理历史表失败", task.getId());
            throw new OperationFailedException(taskName, new Throwable("将失败的文件处理任务, 插入文件处理历史表失败"));
        }

        return Optional.of(true);
    }

}
