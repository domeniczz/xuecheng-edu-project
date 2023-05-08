package com.xuecheng.content.service;

import com.xuecheng.base.model.PageParams;
import com.xuecheng.base.model.PageResult;
import com.xuecheng.base.model.RestResponse;
import com.xuecheng.content.model.dto.AddCourseDto;
import com.xuecheng.content.model.dto.CourseBaseInfoDto;
import com.xuecheng.content.model.dto.QueryCourseParamsDto;
import com.xuecheng.content.model.dto.UpdateCourseDto;
import com.xuecheng.content.model.po.CourseBase;

/**
 * @author Domenic
 * @Classname CourseBaseService
 * @Description 课程信息服务接口
 * @Created by Domenic
 */
public interface CourseBaseInfoService {

    /**
     * 课程分页查询
     * @param pageParams           分页查询参数
     * @param queryCourseParamsDto 课程查询条件
     * @return {@link PageResult}&lt;{@link CourseBase}&gt;
     */
    PageResult<CourseBase> queryCourseBaseList(PageParams pageParams, QueryCourseParamsDto queryCourseParamsDto);

    /**
     * 根据课程 ID 查询课程信息
     * @param courseId 课程 ID
     * @return {@link CourseBaseInfoDto}
     */
    CourseBaseInfoDto queryCourseBaseAndMarketInfoById(Long courseId);

    /**
     * <p>
     * 添加课程基本信息<br/>
     * 审核状态、发布状态会给默认值
     * </p>
     * @param companyId    教学机构 ID
     * @param addCourseDto 课程信息
     * @return {@link CourseBaseInfoDto}
     */
    CourseBaseInfoDto create(Long companyId, AddCourseDto addCourseDto);

    /**
     * 更新课程基本信息
     * @param companyId       教学机构 ID
     * @param updateCourseDto 课程信息
     * @return {@link CourseBaseInfoDto}
     */
    CourseBaseInfoDto update(Long companyId, UpdateCourseDto updateCourseDto);

    /**
     * <p>
     * 删除课程<br/>
     * 课程的审核状态为未提交时方可删除<br/>
     * 会删除课程相关的基本信息、营销信息、课程计划、课程教师信息
     * </p>
     * @param courseId 课程 ID
     * @return 操作结果
     */
    RestResponse<?> delete(Long courseId);

}
