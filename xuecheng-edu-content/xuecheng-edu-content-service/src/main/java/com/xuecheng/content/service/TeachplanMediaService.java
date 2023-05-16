package com.xuecheng.content.service;

/**
 * @author Domenic
 * @Classname TeachplanMediaService
 * @Description 课程计划媒资关联信息服务接口
 * @Created by Domenic
 */
public interface TeachplanMediaService {

    /**
     * 删除课程计划与媒资文件的关联信息 (可能没有关联的媒资信息)
     * @param teachplanId 课程计划 id
     * @return 删除结果
     */
    int deleteTeachplanMedia(Long teachplanId);

}
