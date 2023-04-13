package com.xuecheng.content.service.impl;

import com.xuecheng.content.mapper.CourseMarketMapper;
import com.xuecheng.content.model.po.CourseMarket;
import com.xuecheng.content.service.CourseMarketService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author Domenic
 * @Classname CourseMarketServiceImpl
 * @Description TODO
 * @Date 4/13/2023 2:04 PM
 * @Created by Domenic
 */
@Slf4j
@Service
public class CourseMarketServiceImpl implements CourseMarketService {

    @Autowired
    CourseMarketMapper courseMarketMapper;

    public int saveCourseMarket(CourseMarket courseMarket) {
        // 参数合法性校验
        String charge = courseMarket.getCharge();
        if (StringUtils.isEmpty(charge)) {
            throw new RuntimeException("收费规则为空");
        }

        // 若课程为收费，但价格没有填写，则抛出异常
        if (charge.equals("201001")) {
            if (courseMarket.getPrice() == null || courseMarket.getPrice() <= 0) {
                throw new RuntimeException("课程的价格不能为空并且必须大于0");
                // XueChengPlusException.cast("课程的价格不能为空并且必须大于0");
            }
        }

        // 判断课程营销信息是否存在，若存在则更新，若不存在则新增
        CourseMarket market = courseMarketMapper.selectById(courseMarket.getId());

        if (market != null) {
            BeanUtils.copyProperties(courseMarket, market);
            market.setId(courseMarket.getId());
            return courseMarketMapper.updateById(courseMarket);
        } else {
            return courseMarketMapper.insert(courseMarket);
        }

    }

}
