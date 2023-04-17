package com.xuecheng.content.model.dto;

import com.xuecheng.base.validation.ValidationGroups;
import com.xuecheng.base.validation.constraints.PhoneNumberConstraint;
import com.xuecheng.base.validation.constraints.QQNumberConstraint;
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

    @ApiModelProperty(value = "课程名称", required = true)
    @NotEmpty(message = "课程名称不能为空", groups = { ValidationGroups.Insert.class, ValidationGroups.Update.class, ValidationGroups.Delete.class })
    private String name;

    @ApiModelProperty(value = "适用人群", required = true)
    @NotEmpty(message = "适用人群不能为空", groups = { ValidationGroups.Insert.class, ValidationGroups.Update.class })
    @Size(message = "适用人群内容过少，至少 5 字", min = 5, groups = { ValidationGroups.Insert.class, ValidationGroups.Update.class })
    private String users;

    @ApiModelProperty(value = "课程标签")
    private String tags;

    @ApiModelProperty(value = "大分类", required = true)
    @NotEmpty(message = "课程分类不能为空", groups = { ValidationGroups.Insert.class, ValidationGroups.Update.class })
    private String mt;

    @ApiModelProperty(value = "小分类", required = true)
    @NotEmpty(message = "课程分类不能为空", groups = { ValidationGroups.Insert.class, ValidationGroups.Update.class })
    private String st;

    @ApiModelProperty(value = "课程等级", required = true)
    @NotEmpty(message = "课程等级不能为空", groups = { ValidationGroups.Insert.class, ValidationGroups.Update.class })
    private String grade;

    @ApiModelProperty(value = "教学模式（普通，录播，直播等）", required = true)
    @NotEmpty(message = "教学模式不能为空", groups = { ValidationGroups.Insert.class, ValidationGroups.Update.class })
    private String teachmode;

    @ApiModelProperty(value = "课程介绍")
    @Size(message = "课程介绍内容过少，至少 10 字", min = 10, groups = { ValidationGroups.Insert.class, ValidationGroups.Update.class })
    private String description;

    @ApiModelProperty(value = "课程图片", required = true)
    private String pic;

    @ApiModelProperty(value = "收费规则，对应数据字典", required = true)
    @NotEmpty(message = "收费规则不能为空", groups = { ValidationGroups.Insert.class, ValidationGroups.Update.class })
    private String charge;

    @ApiModelProperty(value = "价格")
    @DecimalMin(value = "0.0", message = "价格不能小于 0", groups = { ValidationGroups.Insert.class, ValidationGroups.Update.class })
    private Float price;

    @ApiModelProperty(value = "原价")
    @DecimalMin(value = "0.0", message = "价格不能小于 0", groups = { ValidationGroups.Insert.class, ValidationGroups.Update.class })
    private Float originalPrice;

    @ApiModelProperty(value = "qq")
    @QQNumberConstraint(message = "QQ 号码不合法", groups = { ValidationGroups.Insert.class, ValidationGroups.Update.class })
    private String qq;

    @ApiModelProperty(value = "微信")
    private String wechat;

    @ApiModelProperty(value = "电话")
    @PhoneNumberConstraint(message = "电话号码不合法", groups = { ValidationGroups.Insert.class, ValidationGroups.Update.class })
    private String phone;

    @ApiModelProperty(value = "有效期")
    private Integer validDays;

}