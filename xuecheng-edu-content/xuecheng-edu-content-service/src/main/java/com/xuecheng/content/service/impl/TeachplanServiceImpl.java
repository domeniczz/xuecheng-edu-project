package com.xuecheng.content.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.xuecheng.base.model.ResponseResult;
import com.xuecheng.content.mapper.TeachplanMapper;
import com.xuecheng.content.model.dto.SaveTeachplanDto;
import com.xuecheng.content.model.dto.TeachplanDto;
import com.xuecheng.content.model.po.Teachplan;
import com.xuecheng.content.service.TeachplanService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
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

    @Override
    public ResponseResult saveTeachplan(SaveTeachplanDto saveTeachplanDto) {
        Long teachplanId = saveTeachplanDto.getId();

        if (teachplanId == null) {
            // 新增
            Teachplan teachplan = new Teachplan();
            BeanUtils.copyProperties(saveTeachplanDto, teachplan);

            teachplan.setCreateDate(LocalDateTime.now());
            teachplan.setChangeDate(LocalDateTime.now());

            Long parentid = saveTeachplanDto.getParentid();
            Long courseId = saveTeachplanDto.getCourseId();
            int teachplanCount = getTeachplanCount(courseId, parentid);
            teachplan.setOrderby(teachplanCount + 1);

            int res = teachplanMapper.insert(teachplan);

            ResponseResult resp = new ResponseResult();
            resp.setStatusCode(res > 0 ? HttpStatus.OK.value() : HttpStatus.INTERNAL_SERVER_ERROR.value());
            resp.setMessage(res > 0 ? "新增课程计划成功" : "新增课程计划失败");

            return resp;
        } else {
            // 修改
            Teachplan teachplan = teachplanMapper.selectById(teachplanId);
            BeanUtils.copyProperties(saveTeachplanDto, teachplan);

            teachplan.setChangeDate(LocalDateTime.now());

            int res = teachplanMapper.updateById(teachplan);

            ResponseResult resp = new ResponseResult();
            resp.setStatusCode(res > 0 ? HttpStatus.OK.value() : HttpStatus.INTERNAL_SERVER_ERROR.value());
            resp.setMessage(res > 0 ? "修改课程计划成功" : "修改课程计划失败");

            return resp;
        }
    }

    /**
     * 获取课程计划数量
     * @param courseId 课程 id
     * @param parentId 父级 id
     * @return 课程计划数量
     */
    private int getTeachplanCount(Long courseId, Long parentId) {
        // 示例 SQL 语句：SELECT COUNT(id) FROM teachplan WHERE course_id = 117 AND parentid = 268
        LambdaQueryWrapper<Teachplan> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper = queryWrapper.eq(Teachplan::getCourseId, courseId).eq(Teachplan::getParentid, parentId);
        Integer count = teachplanMapper.selectCount(queryWrapper);
        return count;
    }

}
