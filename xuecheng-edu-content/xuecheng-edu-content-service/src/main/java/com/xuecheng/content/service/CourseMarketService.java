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
     * 保存课程营销信息（有则更新，无则添加）
     * @param courseMarket 课程营销信息
     * @return 1：更新成功 0：更新失败
     */
    int saveOrUpdateCourseMarket(CourseMarket courseMarket);

}
