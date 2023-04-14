package com.xuecheng.content.service;

import com.xuecheng.content.model.dto.CourseCategoryTreeDto;

import java.util.List;

/**
 * @author Domenic
 * @Classname CourseCategoryService
 * @Description TODO
 * @Date 4/11/2023 8:36 PM
 * @Created by Domenic
 */
public interface CourseCategoryService {

    /**
     * 获取课程分类的属性结构
     * @param rootCourseID 根课程的 id 值
     * @return CourseCategoryTreeDto List
     */
    public List<CourseCategoryTreeDto> queryTreeNodes(String rootCourseID);

}
