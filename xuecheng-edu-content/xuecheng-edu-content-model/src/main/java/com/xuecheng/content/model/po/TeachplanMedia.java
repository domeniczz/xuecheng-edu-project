package com.xuecheng.content.model.po;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import java.io.Serializable;
import java.time.LocalDateTime;

import io.swagger.annotations.ApiModel;
import lombok.Data;

/**
 * @author Domenic
 * @Classname TeachplanMedia
 * @Description 课程计划与媒资文件关联信息
 * @Created by Domenic
 */
@Data
@TableName("teachplan_media")
@ApiModel(value = "TeachplanMedia", description = "课程计划与媒资文件关联信息")
public class TeachplanMedia implements Serializable {

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
     * 媒资文件id
     */
    private String mediaId;

    /**
     * <p>
     * 课程计划标识
     * </p>
     * <!-- 不要改为基本类型，维持包装类 Long -->
     */
    private Long teachplanId;

    /**
     * <p>
     * 课程标识
     * </p>
     * <!-- 不要改为基本类型，维持包装类 Long -->
     */
    private Long courseId;

    /**
     * 媒资文件原始名称
     */
    @TableField("media_fileName")
    private String mediaFilename;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createDate;

    /**
     * 创建人
     */
    private String createPeople;

    /**
     * 修改人
     */
    private String changePeople;

}
