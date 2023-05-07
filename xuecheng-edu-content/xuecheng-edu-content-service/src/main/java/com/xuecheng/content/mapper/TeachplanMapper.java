package com.xuecheng.content.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xuecheng.content.model.dto.TeachplanDto;
import com.xuecheng.content.model.po.Teachplan;

import java.util.List;

/**
 * @author Domenic
 * @Classname TeachplanMapper
 * @Description 课程计划 Mapper 接口
 * @Created by Domenic
 */
public interface TeachplanMapper extends BaseMapper<Teachplan> {

    /**
     * <p>
     * 使用递归查询课程计划树形结构<br/>
     * 在数据库中，课程计划的数据是以树形结构组织起来的<br/>
     * 因此采用递归，查询时需提供根节点的 id 值
     * </p>
     * @param courseId 根课程的 id 值
     * @return {@link TeachplanDto}
     */
    public List<TeachplanDto> selectTreeNodes(long courseId);

}
