package com.xuecheng.content.service.impl;

import com.xuecheng.content.mapper.TeachplanMediaMapper;
import com.xuecheng.content.service.TeachplanMediaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author Domenic
 * @Classname TeachplanMediaServiceImpl
 * @Description 课程计划媒资管理服务接口实现类
 * @Created by Domenic
 */
@Service
@Transactional
public class TeachplanMediaServiceImpl implements TeachplanMediaService {

    @Autowired
    private TeachplanMediaMapper teachplanMediaMapper;

    @Override
    public int deleteTeachplanMedia(long teachplanId) {
        // SQL: DELETE FROM teachplan_media WHERE teachplan_id = #{id}
        return teachplanMediaMapper.deleteById(teachplanId);
    }

}
