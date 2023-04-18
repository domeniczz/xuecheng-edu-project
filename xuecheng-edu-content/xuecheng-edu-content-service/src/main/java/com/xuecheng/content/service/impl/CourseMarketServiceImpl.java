package com.xuecheng.content.service.impl;

import com.xuecheng.base.exception.XueChengEduException;
import com.xuecheng.content.mapper.CourseMarketMapper;
import com.xuecheng.content.model.po.CourseMarket;
import com.xuecheng.content.service.CourseMarketService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author Domenic
 * @Classname CourseMarketServiceImpl
 * @Description 课程营销信息服务接口实现类
 * @Created by Domenic
 */
@Slf4j
@Service
@Transactional
public class CourseMarketServiceImpl implements CourseMarketService {

    @Autowired
    private CourseMarketMapper courseMarketMapper;

    @Override
    public int saveOrUpdateCourseMarket(CourseMarket courseMarket) {
        // 参数合法性校验在 controller 层已经做过

        // 若课程为收费，但价格没有填写，则抛出异常
        String charge = courseMarket.getCharge();
        if (charge.equals("201001")) {
            if (courseMarket.getPrice() == null || courseMarket.getPrice() <= 0) {
                XueChengEduException.cast("课程的价格不能为空，且必须大于 0");
                return -1;
            }
        }

        // 判断课程营销信息是否存在，若存在则更新，若不存在则新增
        CourseMarket market = courseMarketMapper.selectById(courseMarket.getId());

        if (market != null) {
            // 更新
            BeanUtils.copyProperties(courseMarket, market);
            market.setId(courseMarket.getId());
            return courseMarketMapper.updateById(courseMarket);
        } else {
            // 新增
            return courseMarketMapper.insert(courseMarket);
        }

    }

}
