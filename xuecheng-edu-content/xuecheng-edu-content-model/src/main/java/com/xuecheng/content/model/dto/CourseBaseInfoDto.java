package com.xuecheng.content.model.dto;

import com.xuecheng.content.model.po.CourseBase;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author Domenic
 * @Classname CourseBaseInfoDto
 * @Description 课程信息DTO（课程基本信息 + 课程营销信息）
 * @Created by Domenic
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class CourseBaseInfoDto extends CourseBase {

    private static final long serialVersionUID = 1L;

    /**
     * 收费规则，对应数据字典
     */
    private String charge;

    /**
     * 价格
     */
    private Float price;

    /**
     * 原价
     */
    private Float originalPrice;

    /**
     * 咨询qq
     */
    private String qq;

    /**
     * 微信
     */
    private String wechat;

    /**
     * 电话
     */
    private String phone;

    /**
     * 有效期天数
     */
    private Integer validDays;

    /**
     * 大分类名称（通过 CourseBase 中的 mt 码查询出大分类名称）
     */
    private String mtName;

    /**
     * 小分类名称（通过 CourseBase 中的 st 码查询出小分类名称）
     */
    private String stName;

}
