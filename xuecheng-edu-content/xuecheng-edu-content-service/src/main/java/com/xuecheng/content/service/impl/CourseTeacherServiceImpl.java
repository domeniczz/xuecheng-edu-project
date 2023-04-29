package com.xuecheng.content.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.xuecheng.base.exception.XueChengEduException;
import com.xuecheng.base.model.ResponseResult;
import com.xuecheng.content.mapper.CourseTeacherMapper;
import com.xuecheng.content.model.po.CourseTeacher;
import com.xuecheng.content.service.CourseTeacherService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

/**
 * @author Domenic
 * @Classname CourseTeacherServiceImpl
 * @Description 课程师资管理服务接口实现类
 * @Created by Domenic
 */
@Service
public class CourseTeacherServiceImpl implements CourseTeacherService {

    @Autowired
    CourseTeacherMapper courseTeacherMapper;

    @Override
    public List<CourseTeacher> queryTeacherList(long courseId) {
        return courseTeacherMapper.selectList(new LambdaQueryWrapper<CourseTeacher>()
                .eq(CourseTeacher::getCourseId, courseId));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public CourseTeacher save(CourseTeacher teacher) {
        if (teacher.getId() == null) {
            return create(teacher);
        } else {
            return update(teacher);
        }
    }

    private CourseTeacher create(CourseTeacher teacher) {
        teacher.setCreateDate(LocalDateTime.now());
        int res = courseTeacherMapper.insert(teacher);
        if (res > 0) {
            return courseTeacherMapper.selectById(teacher.getId());
        } else {
            XueChengEduException.cast("创建课程师资失败");
            return null;
        }
    }

    private CourseTeacher update(CourseTeacher teacher) {
        int res = courseTeacherMapper.updateById(teacher);
        if (res > 0) {
            return courseTeacherMapper.selectById(teacher.getId());
        } else {
            XueChengEduException.cast("更新课程师资失败");
            return null;
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ResponseResult delete(long courseId, long teacherId) {
        int res = courseTeacherMapper.delete(new LambdaQueryWrapper<CourseTeacher>()
                .eq(CourseTeacher::getCourseId, courseId)
                .eq(CourseTeacher::getId, teacherId));
        if (res > 0) {
            return new ResponseResult(HttpStatus.OK.value(), "删除师资成功");
        } else {
            XueChengEduException.cast("删除师资失败");
            return null;
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ResponseResult deleteAll(long courseId) {
        courseTeacherMapper.delete(new LambdaQueryWrapper<CourseTeacher>().eq(CourseTeacher::getCourseId, courseId));
        return new ResponseResult(HttpStatus.OK.value(), "删除课程对应的所有教师信息成功");
    }

}
