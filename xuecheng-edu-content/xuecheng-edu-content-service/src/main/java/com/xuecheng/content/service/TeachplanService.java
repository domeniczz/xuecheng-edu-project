package com.xuecheng.content.service;

import com.xuecheng.base.model.ResponseResult;
import com.xuecheng.content.model.dto.SaveTeachplanDto;
import com.xuecheng.content.model.dto.TeachplanDto;

import java.util.List;

/**
 * @author Domenic
 * @Classname TeachplanService
 * @Description 课程计划服务接口
 * @Created by Domenic
 */
public interface TeachplanService {

    /**
     * 获取课程计划的树形结构
     * @param courseId 根节点课程 id
     * @return TeachplanDto List
     */
    public List<TeachplanDto> queryTreeNodes(long courseId);

    /**
     * 添加/修改课程计划
     * @param saveTeachplanDto 保存课程计划 dto
     * @return 添加结果
     */
    public ResponseResult saveTeachplan(SaveTeachplanDto saveTeachplanDto);

}
