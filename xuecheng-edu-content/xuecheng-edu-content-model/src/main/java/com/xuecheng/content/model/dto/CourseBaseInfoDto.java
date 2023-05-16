package com.xuecheng.content.model.dto;

import com.xuecheng.content.model.po.CourseBase;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 * @author Domenic
 * @Classname CourseBaseInfoDto
 * @Description 课程信息DTO (课程基本信息 + 课程营销信息)
 * @Created by Domenic
 */
@Data
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@ApiModel(value = "CourseBaseInfoDto", description = "课程信息")
public class CourseBaseInfoDto extends CourseBase {

    private static final long serialVersionUID = 1L;

    /**
     * 课程收费规则，对应数据字典
     */
    @ApiModelProperty("课程收费规则")
    private String charge;

    /**
     * 课程价格
     */
    @ApiModelProperty("课程价格")
    private Double price;

    /**
     * 课程原价
     */
    @ApiModelProperty("课程原价")
    private Double originalPrice;

    /**
     * 咨询 QQ 号码
     */
    @ApiModelProperty("咨询 QQ 号码")
    private String qq;

    /**
     * 咨询微信 ID
     */
    @ApiModelProperty("咨询微信 ID")
    private String wechat;

    /**
     * 咨询电话号码
     */
    @ApiModelProperty("咨询电话号码")
    private String phone;

    /**
     * 课程有效期天数
     */
    @ApiModelProperty("课程有效期天数")
    private Integer validDays;

    /**
     * 课程大分类名称 (通过 CourseBase 中的 mt 码查询出大分类名称)
     */
    @ApiModelProperty("课程大分类名称")
    private String mtName;

    /**
     * 课程小分类名称 (通过 CourseBase 中的 st 码查询出小分类名称)
     */
    @ApiModelProperty("课程小分类名称")
    private String stName;

}
