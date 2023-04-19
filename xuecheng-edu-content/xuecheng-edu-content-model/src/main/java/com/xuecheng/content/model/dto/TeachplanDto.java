package com.xuecheng.content.model.dto;

import com.xuecheng.content.model.po.Teachplan;
import com.xuecheng.content.model.po.TeachplanMedia;
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
public class TeachplanDto extends Teachplan {

    /**
     * 与媒资管理的信息
     */
    private TeachplanMedia teachplanMedia;

    /**
     * 小章节的 list
     */
    private List<TeachplanDto> teachPlanTreeNodes;

}
