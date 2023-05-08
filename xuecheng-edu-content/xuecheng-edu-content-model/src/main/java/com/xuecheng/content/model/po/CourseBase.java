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
 * @Classname CourseBase
 * @Description 课程基本信息
 * @Created by Domenic
 */
@Data
@TableName("course_base")
@ApiModel(value = "CourseBase", description = "课程基本信息")
public class CourseBase implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * <p>
     * 主键
     * </p>
     * <!-- 不要改为基本类型，维持包装类 Long -->
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * <p>
     * 机构 ID
     * </p>
     * <!-- 不要改为基本类型，维持包装类 Long -->
     */
    private Long companyId;

    /**
     * 机构名称
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
     * 课程标签
     */
    private String tags;

    /**
     * 大分类
     */
    private String mt;

    /**
     * 小分类
     */
    private String st;

    /**
     * 课程等级
     */
    private String grade;

    /**
     * 教育模式 (common 普通，record 录播，live 直播等）
     */
    private String teachmode;

    /**
     * 课程介绍
     */
    private String description;

    /**
     * 课程图片
     */
    private String pic;

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

    /**
     * 创建人
     */
    private String createPeople;

    /**
     * 更新人
     */
    private String changePeople;

    /**
     * 审核状态
     */
    private String auditStatus;

    /**
     * 课程发布状态: 未发布 已发布 下线
     */
    private String status;

}
