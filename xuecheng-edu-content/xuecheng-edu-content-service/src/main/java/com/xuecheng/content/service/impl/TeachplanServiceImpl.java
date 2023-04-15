package com.xuecheng.content.service.impl;

import com.xuecheng.content.mapper.TeachplanMapper;
import com.xuecheng.content.model.dto.TeachplanDto;
import com.xuecheng.content.service.TeachplanService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author Domenic
 * @Classname TeachplanServiceImpl
 * @Description 课程计划服务接口实现
 * @Created by Domenic
 */
@Service
public class TeachplanServiceImpl implements TeachplanService {

    @Autowired
    private TeachplanMapper teachplanMapper;

    @Override
    public List<TeachplanDto> queryTreeNodes(long courseId) {
        return teachplanMapper.selectTreeNodes(courseId);
    }

    // @Override
    // public boolean saveTeachplan(SaveTeachplanDto saveTeachplanDto) {
    //     return false;
    // }

}
