package com.xuecheng.content.model.po;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * @author Domenic
 * @Classname Teachplan
 * @Description 课程计划信息
 * @Created by Domenic
 */
@Data
@TableName("teachplan")
@ApiModel(value = "Teachplan", description = "课程计划信息")
public class Teachplan implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * <!-- 不要改为基本类型，维持包装类 Long -->
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 课程计划名称
     */
    private String pname;

    /**
     * <p>
     * 课程计划父级 ID
     * </p>
     * <!-- 不要改为基本类型，维持包装类 Long -->
     */
    private Long parentid;

    /**
     * <p>
     * 层级，分为 1、2、3 级
     * </p>
     * <!-- 不要改为基本类型，维持包装类 Long -->
     */
    private Integer grade;

    /**
     * 课程类型: 1 视频, 2 文档
     */
    private String mediaType;

    /**
     * 开始直播时间
     */
    private LocalDateTime startTime;

    /**
     * 直播结束时间
     */
    private LocalDateTime endTime;

    /**
     * 章节及课程时介绍
     */
    private String description;

    /**
     * 时长，单位时:分:秒
     */
    private String timelength;

    /**
     * <p>
     * 排序字段
     * </p>
     * <!-- 不要改为基本类型，维持包装类 Integer -->
     */
    private Integer orderby;

    /**
     * <p>
     * 课程标识
     * </p>
     * <!-- 不要改为基本类型，维持包装类 Long -->
     */
    private Long courseId;

    /**
     * <p>
     * 课程发布标识
     * </p>
     * <!-- 不要改为基本类型，维持包装类 Long -->
     */
    private Long coursePubId;

    /**
     * <p>
     * 状态: 1 正常, 0 删除
     * </p>
     * <!-- 不要改为基本类型，维持包装类 Integer -->
     */
    private Integer status;

    /**
     * 是否支持试学或预览（试看）
     */
    private String isPreview;

    /**
     * 创建时间
     */
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createDate;

    /**
     * 修改时间
     */
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime changeDate;

}
