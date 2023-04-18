package com.xuecheng.content.model.po;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.xuecheng.base.validation.ValidationGroups;

import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;

/**
 * 课程-教师关系表
 */
@Data
@TableName("course_teacher")
public class CourseTeacher implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键
     */
    @TableId(value = "id", type = IdType.AUTO)
    @Min(value = 0L, message = "主键 ID 不能小于 0")
    private Long id;

    /**
     * 课程标识
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
