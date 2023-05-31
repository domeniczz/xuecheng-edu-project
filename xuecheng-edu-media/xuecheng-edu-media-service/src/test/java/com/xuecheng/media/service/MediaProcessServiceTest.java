package com.xuecheng.media.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.xuecheng.media.mapper.MediaFileMapper;
import com.xuecheng.media.mapper.MediaProcessHistoryMapper;
import com.xuecheng.media.mapper.MediaProcessMapper;
import com.xuecheng.media.model.po.MediaFile;
import com.xuecheng.media.model.po.MediaProcess;
import com.xuecheng.media.model.po.MediaProcessHistory;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * @author Domenic
 * @Classname MediaProcessServiceTest
 * @Description 文件处理服务接口测试类
 * @Created by Domenic
 */
@SpringBootTest
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class MediaProcessServiceTest {

    @Autowired
    private MediaProcessService mediaProcessService;

    @Autowired
    private MediaProcessMapper mediaProcessMapper;

    @Autowired
    private MediaProcessHistoryMapper mediaProcessHistoryMapper;

    @Autowired
    private MediaFileMapper mediaFileMapper;

    @Value("${media.processing.maxfailcount}")
    private int maxFailCount;

    private List<MediaProcess> taskList = null;

    private final String url[] = new String[2];
    private final MediaFile mediaFiles[] = new MediaFile[2];

    @BeforeAll
    public void init() {
        String fileId1 = generateRandomMd5(32);
        String fileId2 = generateRandomMd5(32);

        String bucketName = "video";
        String filename = "mockVideo";
        String fileExtension = ".avi";
        String fileObjectName1 = fileId1.charAt(0) + "/" + fileId1.charAt(1) + "/" + fileId1 + "/" + filename + "-" + fileId1 + fileExtension;
        String fileObjectName2 = fileId1.charAt(0) + "/" + fileId1.charAt(1) + "/" + fileId1 + "/" + filename + "-" + fileId1 + fileExtension;

        url[0] = "/" + bucketName + "/" + fileObjectName1;
        url[1] = "/" + bucketName + "/" + fileObjectName2;

        MediaFile mediaFile1 = new MediaFile();
        mediaFile1.setId(fileId1);
        mediaFile1.setCompanyId(123456789L);
        mediaFile1.setCompanyName("Mock Company");
        mediaFile1.setFilename(filename + fileId1 + fileExtension);
        mediaFile1.setFileType("001002");
        mediaFile1.setTags("tag1,tag2,tag3");
        mediaFile1.setBucket(bucketName);
        mediaFile1.setFilePath(fileObjectName1);
        mediaFile1.setFileId(fileId1);
        mediaFile1.setUrl(null);
        mediaFile1.setUsername("Mock Username");
        mediaFile1.setCreateDate(LocalDateTime.now());
        mediaFile1.setChangeDate(LocalDateTime.now());
        mediaFile1.setStatus("1");
        mediaFile1.setRemark("Mock Remark");
        mediaFile1.setAuditStatus("002003");
        mediaFile1.setAuditMind("Mock Audit Mind");
        mediaFile1.setFileSize(26758769L);

        MediaFile mediaFile2 = new MediaFile();
        BeanUtils.copyProperties(mediaFile1, mediaFile2);
        mediaFile2.setId(fileId2);
        mediaFile2.setFileId(fileId2);
        mediaFile2.setFilename(filename + fileId2 + fileExtension);

        mediaFileMapper.insert(mediaFile1);
        mediaFileMapper.insert(mediaFile2);

        mediaFiles[0] = mediaFile1;
        mediaFiles[1] = mediaFile2;
    }

    @Test
    @Order(2)
    void testGetTaskListForShard() {
        int shardIndex = 0;
        int shardTotal = 1;
        int taskCount = 2;

        taskList = mediaProcessService.getTaskListForShard(shardIndex, shardTotal, taskCount);

        Assertions.assertNotNull(taskList, "获取待处理任务列表失败");
        Assertions.assertEquals(taskCount, taskList.size(), "获取待处理任务数量出错");
        Assertions.assertEquals(mediaFiles[0].getFileId(), taskList.get(0).getFileId(), "获取的待处理任务 1 的信息不正确");
        Assertions.assertEquals(mediaFiles[1].getFileId(), taskList.get(1).getFileId(), "获取的待处理任务 2 的信息不正确");
    }

    @Test
    @Order(1)
    void testAddPendingTask() {
        boolean res1 = mediaProcessService.addPendingTask(mediaFiles[0]);
        boolean res2 = mediaProcessService.addPendingTask(mediaFiles[1]);
        Assertions.assertTrue(res1, "添加待处理任务 1 失败");
        Assertions.assertTrue(res2, "添加待处理任务 2 失败");
    }

    @Test
    @Order(3)
    void testStartTask() {
        taskList.forEach(mediaProcess -> {
            boolean res = mediaProcessService.startTask(mediaProcess);
            Assertions.assertTrue(res, "启动处理任务失败, fileId=" + mediaProcess.getFileId());
        });
    }

    @Test
    @Order(4)
    void testHandleSucceededTask() {
        MediaProcess mediaProcess = taskList.get(0);
        mediaProcess.setUrl(url[0]);

        Assertions.assertFalse(maxFailCount < mediaProcess.getFailCount(), "文件处理失败次数超过限制 " + maxFailCount + ", fileId=" + mediaProcess.getFileId());
        Optional<Boolean> res = mediaProcessService.handleSucceededTask(mediaProcess);
        Assertions.assertTrue(res.isPresent(), "转储执行成功信息失败, fileId=" + mediaProcess.getFileId());

        MediaProcess resSelect = mediaProcessMapper.selectOne(new LambdaQueryWrapper<MediaProcess>()
                .eq(MediaProcess::getFileId, mediaProcess.getFileId()));
        Assertions.assertNull(resSelect, "获取转储执行成功信息失败, 未删除待执行任务, fileId=" + mediaProcess.getFileId());
    }

    @Test
    @Order(5)
    void testHandleFailedTask() {
        MediaProcess mediaProcess = taskList.get(1);
        mediaProcess.setUrl(url[1]);

        mediaProcess.setFailCount(maxFailCount);
        Optional<Boolean> res = mediaProcessService.handleFailedTask(mediaProcess);
        Assertions.assertTrue(res.isPresent(), "转储执行成功信息失败, fileId=" + mediaProcess.getFileId());

        MediaProcess resSelect = mediaProcessMapper.selectOne(new LambdaQueryWrapper<MediaProcess>()
                .eq(MediaProcess::getFileId, mediaProcess.getFileId()));
        Assertions.assertEquals("3", resSelect.getStatus(), "获取转储执行失败信息失败, 未删除待执行任务, fileId=" + mediaProcess.getFileId());
    }

    @AfterAll
    public void tearDown() {
        mediaFileMapper.deleteById(mediaFiles[0].getFileId());
        mediaFileMapper.deleteById(mediaFiles[1].getFileId());

        mediaProcessMapper.delete(new LambdaQueryWrapper<MediaProcess>().eq(MediaProcess::getFileId, mediaFiles[1].getFileId()));

        int res = mediaProcessHistoryMapper.delete(new LambdaQueryWrapper<MediaProcessHistory>()
                .eq(MediaProcessHistory::getFileId, mediaFiles[0].getFileId()));
        Assertions.assertEquals(1, res, "删除任务处理历史失败, fileId=" + taskList.get(0).getFileId());
    }

    public static String generateRandomMd5(int length) {
        SecureRandom random = new SecureRandom();
        String characters = "0123456789abcdefghijklmnopqrstuvwxyz";

        StringBuilder builder = new StringBuilder(length);
        for (int i = 0; i < length; ++i) {
            builder.append(characters.charAt(random.nextInt(characters.length())));
        }

        return builder.toString();
    }

}
