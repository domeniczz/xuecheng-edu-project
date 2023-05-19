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
     * <p>
     * 课程教学计划 ID
     * </p>
     * <!-- 不要改为基本类型，维持包装类 Long -->
     */
    @ApiModelProperty(value = "课程教学计划 ID")
    private Long id;

    /**
     * 课程计划名称
     */
    @ApiModelProperty(value = "课程计划名称", required = true)
    private String pname;

    /**
     * <p>
     * 课程计划父级 ID
     * </p>
     * <!-- 不要改为基本类型，维持包装类 Long -->
     */
    @ApiModelProperty(value = "课程计划父级 ID")
    private Long parentid;

    /**
     * <p>
     * 层级，分为 1、2、3 级
     * </p>
     * <!-- 不要改为基本类型，维持包装类 Integer -->
     */
    @ApiModelProperty(value = "层级，分为 1、2、3 级")
    private Integer grade;

    /**
     * 课程类型：1 视频、2 文档
     */
    @ApiModelProperty(value = "课程类型: 1 视频, 2 文档")
    private String mediaType;

    /**
     * <p>
     * 课程标识
     * </p>
     * <!-- 不要改为基本类型，维持包装类 Long -->
     */
    @ApiModelProperty(value = "课程标识")
    private Long courseId;

    /**
     * <p>
     * 课程发布标识
     * </p>
     * <!-- 不要改为基本类型，维持包装类 Long -->
     */
    @ApiModelProperty(value = "课程发布标识")
    private Long coursePubId;

    /**
     * 是否支持试学或预览 (试看)
     */
    @ApiModelProperty(value = "是否支持试学或预览")
    private String isPreview;

}
