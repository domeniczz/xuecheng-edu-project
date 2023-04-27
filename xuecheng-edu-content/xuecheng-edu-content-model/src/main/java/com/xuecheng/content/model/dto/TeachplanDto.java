package com.xuecheng.content.model.dto;

import com.xuecheng.content.model.po.Teachplan;
import com.xuecheng.content.model.po.TeachplanMedia;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.util.List;

/**
 * @author Domenic
 * @Classname TeachplanDto
 * @Description 课程计划数据传输对象
 * @Created by Domenic
 */
@Data
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = false)
@ApiModel(value = "TeachplanDto", description = "课程计划数据传输对象")
public class TeachplanDto extends Teachplan {

    /**
     * 与媒资管理关联的信息
     */
    @ApiModelProperty("与媒资管理关联的信息")
    private TeachplanMedia teachplanMedia;

    /**
     * 小章节的 List
     */
    @ApiModelProperty("小章节的 List")
    private List<TeachplanDto> teachPlanTreeNodes;

}
