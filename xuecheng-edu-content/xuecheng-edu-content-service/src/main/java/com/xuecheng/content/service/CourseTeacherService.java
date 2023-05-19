package com.xuecheng.content.service;

import com.xuecheng.base.model.RestResponse;
import com.xuecheng.content.model.po.CourseTeacher;

import java.util.List;

/**
 * @author Domenic
 * @Classname CourseTeacherService
 * @Description 课程师资管理服务接口
 * @Created by Domenic
 */
public interface CourseTeacherService {

    /**
     * 查询课程师资列表
     * @param courseId 课程 id
     * @return 课程师资列表
     */
    List<CourseTeacher> queryTeacherList(Long courseId);

    /**
     * 保存课程师资(新增或更新)
     * @param teacher 师资信息
     * @return 保存的师资
     */
    CourseTeacher save(CourseTeacher teacher);

    /**
     * 删除课程师资
     * @param courseId 课程 id
     * @param teacherId 师资 id
     * @return 删除结果
     */
    RestResponse<Object> delete(Long courseId, Long teacherId);

    /**
     * <p>
     * 删除指定课程的所有教师信息<br/>
     * 可能课程没有对应的教师信息，也可能有多个
     * </p>
     * @param courseId 课程 id
     * @return 删除结果
     */
    RestResponse<Object> deleteAll(Long courseId);

}
