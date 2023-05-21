package com.xuecheng.content.model.po;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import java.io.Serializable;
import java.time.LocalDateTime;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;

import io.swagger.annotations.ApiModel;
import lombok.Data;

/**
 * @author Domenic
 * @Classname CourseTeacher
 * @Description 课程-教师关系信息
 * @Created by Domenic
 */
@Data
@TableName("course_teacher")
@ApiModel(value = "CourseTeacher", description = "课程-教师关系信息")
public class CourseTeacher implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * <p>
     * 主键
     * </p>
     * <!-- 不要改为基本类型，维持包装类 Long -->
     */
    @TableId(value = "id", type = IdType.AUTO)
    @Min(value = 0L, message = "主键 ID 不能小于 0")
    private Long id;

    /**
     * <p>
     * 课程标识
     * </p>
     * <!-- 不要改为基本类型，维持包装类 Long -->
     */
    @Min(value = 0L, message = "课程 ID 不能小于 0")
    private Long courseId;

    /**
     * 教师标识
     */
    @NotEmpty(message = "教师名称不能为空")
    private String teacherName;

    /**
     * 教师职位
     */
    @NotEmpty(message = "教师职位不能为空")
    private String position;

    /**
     * 教师简介
     */
    @Size(message = "教师介绍内容过少，至少 10 字", min = 10)
    private String introduction;

    /**
     * 照片
     */
    private String photograph;

    /**
     * 创建时间
     */
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createDate;

}
