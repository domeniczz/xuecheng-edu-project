package com.xuecheng.content.model.po;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * @author Domenic
 * @Classname CoursePublishPre
 * @Description 课程发布信息
 * @Created by Domenic
 */
@Data
@TableName("course_publish_pre")
@ApiModel(value = "CoursePublishPre", description = "课程发布信息")
public class CoursePublishPre implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * <p>
     * 主键
     * </p>
     * <!-- 不要改为基本类型，维持包装类 Long -->
     */
    private Long id;

    /**
     * <p>
     * 机构 ID
     * </p>
     * <!-- 不要改为基本类型，维持包装类 Long -->
     */
    private Long companyId;

    /**
     * 公司名称
     */
    private String companyName;

    /**
     * 课程名称
     */
    private String name;

    /**
     * 适用人群
     */
    private String users;

    /**
     * 标签
     */
    private String tags;

    /**
     * 创建人
     */
    private String username;

    /**
     * 大分类
     */
    private String mt;

    /**
     * 大分类名称
     */
    private String mtName;

    /**
     * 小分类
     */
    private String st;

    /**
     * 小分类名称
     */
    private String stName;

    /**
     * 课程等级
     */
    private String grade;

    /**
     * 教育模式
     */
    private String teachmode;

    /**
     * 课程图片
     */
    private String pic;

    /**
     * 课程介绍
     */
    private String description;

    /**
     * 课程营销信息，json格式
     */
    private String market;

    /**
     * 所有课程计划，json格式
     */
    private String teachplan;

    /**
     * 教师信息，json格式
     */
    private String teachers;

    /**
     * 提交时间
     */
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createDate;

    /**
     * 审核时间
     */
    private LocalDateTime auditDate;

    /**
     * 状态
     */
    private String status;

    /**
     * 备注
     */
    private String remark;

    /**
     * 收费规则，对应数据字典--203
     */
    private String charge;

    /**
     * 现价
     */
    private Double price;

    /**
     * <p>
     * 原价
     * </p>
     * <!-- 不要改为基本类型，维持包装类 Double -->
     */
    private Double originalPrice;

    /**
     * <p>
     * 课程有效期天数
     * </p>
     * <!-- 不要改为基本类型，维持包装类 Integer -->
     */
    private Integer validDays;

}
