package com.xuecheng.content.service;

import com.xuecheng.content.model.dto.CourseCategoryTreeDto;
import com.xuecheng.content.model.po.CourseCategory;

import java.util.List;

/**
 * @author Domenic
 * @Classname CourseCategoryService
 * @Description 课程分类服务接口
 * @Created by Domenic
 */
public interface CourseCategoryService {

    /**
     * 获取课程分类的树形结构
     * @return CourseCategoryTreeDto List
     */
    List<CourseCategoryTreeDto> queryTreeNodes();

    /**
     * 根据课程分类 id 查询课程分类信息
     * @param id 课程分类 id
     * @return 课程分类信息
     */
    CourseCategory query(String categoryId);

}
