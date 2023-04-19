package com.xuecheng.content.service;

import com.xuecheng.content.model.po.CourseMarket;

/**
 * @author Domenic
 * @Classname CourseMarketService
 * @Description 课程营销信息服务接口
 * @Created by Domenic
 */
public interface CourseMarketService {

    /**
     * 根据课程 id 查询课程营销信息
     * @param courseId 课程 id
     * @return 课程营销信息
     */
    CourseMarket query(long courseId);

    /**
     * 保存课程营销信息（有则更新，无则添加）
     * @param courseMarket 课程营销信息
     * @return 1：更新成功 0：更新失败
     */
    CourseMarket saveCourseMarket(CourseMarket courseMarket);

    /**
     * 删除课程 id 对应的课程营销信息
     * @param courseId 课程id
     * @return 1：删除成功 0：删除失败
     */
    int delete(long courseId);

}
