package com.xuecheng.content.model.dto;

import com.xuecheng.base.validation.ValidationGroups;
import com.xuecheng.base.validation.constraints.PhoneNumberConstraint;
import com.xuecheng.base.validation.constraints.QqNumberConstraint;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;

/**
 * @author Domenic
 * @Classname AddCourseDto
 * @Description 添加课程的 DTO
 * @Created by Domenic
 */
@Data
@ApiModel(value = "AddCourseDto", description = "新增课程基本信息")
public class AddCourseDto {

    /**
     * 课程名称
     */
    @ApiModelProperty(value = "课程名称", required = true)
    @NotEmpty(message = "课程名称不能为空", groups = { ValidationGroups.Insert.class, ValidationGroups.Update.class, ValidationGroups.Delete.class })
    private String name;

    /**
     * 课程适用人群
     */
    @ApiModelProperty(value = "课程适用人群", required = true)
    @NotEmpty(message = "适用人群不能为空", groups = { ValidationGroups.Insert.class, ValidationGroups.Update.class })
    @Size(message = "适用人群内容过少，至少 5 字", min = 5, groups = { ValidationGroups.Insert.class, ValidationGroups.Update.class })
    private String users;

    /**
     * 课程标签
     */
    @ApiModelProperty(value = "课程标签")
    private String tags;

    /**
     * 课程大分类
     */
    @ApiModelProperty(value = "课程大分类", required = true)
    @NotEmpty(message = "课程分类不能为空", groups = { ValidationGroups.Insert.class, ValidationGroups.Update.class })
    private String mt;

    /**
     * 课程小分类
     */
    @ApiModelProperty(value = "课程小分类", required = true)
    @NotEmpty(message = "课程分类不能为空", groups = { ValidationGroups.Insert.class, ValidationGroups.Update.class })
    private String st;

    /**
     * 课程等级
     */
    @ApiModelProperty(value = "课程等级", required = true)
    @NotEmpty(message = "课程等级不能为空", groups = { ValidationGroups.Insert.class, ValidationGroups.Update.class })
    private String grade;

    /**
     * 课程教学模式
     */
    @ApiModelProperty(value = "课程教学模式（普通，录播，直播等）", required = true)
    @NotEmpty(message = "教学模式不能为空", groups = { ValidationGroups.Insert.class, ValidationGroups.Update.class })
    private String teachmode;

    /**
     * 课程介绍
     */
    @ApiModelProperty(value = "课程介绍")
    @Size(message = "课程介绍内容过少，至少 10 字", min = 10, groups = { ValidationGroups.Insert.class, ValidationGroups.Update.class })
    private String description;

    /**
     * 课程图片
     */
    @ApiModelProperty(value = "课程图片", required = true)
    private String pic;

    /**
     * 课程收费规则
     */
    @ApiModelProperty(value = "收费规则，对应数据字典", required = true)
    @NotEmpty(message = "收费规则不能为空", groups = { ValidationGroups.Insert.class, ValidationGroups.Update.class })
    private String charge;

    /**
     * 课程价格
     */
    @ApiModelProperty(value = "课程价格")
    @DecimalMin(value = "0.0", message = "价格不能小于 0", groups = { ValidationGroups.Insert.class, ValidationGroups.Update.class })
    private Double price;

    /**
     * 课程原价
     */
    @ApiModelProperty(value = "课程原价")
    @DecimalMin(value = "0.0", message = "价格不能小于 0", groups = { ValidationGroups.Insert.class, ValidationGroups.Update.class })
    private Double originalPrice;

    /**
     * 咨询 QQ 号码
     */
    @ApiModelProperty(value = "咨询 QQ 号码")
    @QqNumberConstraint(message = "QQ 号码不合法", groups = { ValidationGroups.Insert.class, ValidationGroups.Update.class })
    private String qq;

    /**
     * 咨询微信 ID
     */
    @ApiModelProperty(value = "咨询微信 ID")
    private String wechat;

    /**
     * 咨询电话号码
     */
    @ApiModelProperty(value = "咨询电话号码")
    @PhoneNumberConstraint(message = "电话号码不合法", groups = { ValidationGroups.Insert.class, ValidationGroups.Update.class })
    private String phone;

    /**
     * 课程有效期
     */
    @ApiModelProperty(value = "课程有效期")
    private Integer validDays;

}