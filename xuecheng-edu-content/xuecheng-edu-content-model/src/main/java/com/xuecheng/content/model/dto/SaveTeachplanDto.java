package com.xuecheng.content.model.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author domeniczz
 * @Classname SaveTeachplanDto
 * @Description 保存课程计划 DTO
 * @Created by domeniczz
 */
@Data
@ApiModel(value = "SaveTeachplanDto", description = "保存课程计划")
public class SaveTeachplanDto {

    /***
     * 教学计划 id
     */
    @ApiModelProperty(value = "教学计划 id")
    private Long id;

    /**
     * 课程计划名称
     */
    @ApiModelProperty(value = "课程计划名称", required = true)
    private String pname;

    /**
     * 课程计划父级 id
     */
    @ApiModelProperty(value = "课程计划父级 id")
    private Long parentid;

    /**
     * 层级，分为 1、2、3 级
     */
    @ApiModelProperty(value = "层级，分为 1、2、3 级")
    private Integer grade;

    /**
     * 课程类型:1视频、2文档
     */
    @ApiModelProperty(value = "课程类型:1视频、2文档")
    private String mediaType;

    /**
     * 课程标识
     */
    @ApiModelProperty(value = "课程标识")
    private Long courseId;

    /**
     * 课程发布标识
     */
    @ApiModelProperty(value = "课程发布标识")
    private Long coursePubId;

    /**
     * 是否支持试学或预览（试看）
     */
    @ApiModelProperty(value = "是否支持试学或预览")
    private String isPreview;

}
