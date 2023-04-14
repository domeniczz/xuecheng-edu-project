package com.xuecheng.content.model.dto;

import com.xuecheng.content.model.po.CourseCategory;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.util.List;

/**
 * @author Domenic
 * @Classname CourseCategoryTreeDto
 * @Description 课程分类的 DTO
 * @Date 4/11/2023 4:16 PM
 * @Created by Domenic
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class CourseCategoryTreeDto extends CourseCategory implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 子节点的 List<br/>
     * CourseCategory 是以树形结构存储的，因此需要存储子节点
     */
    private List<CourseCategory> childrenTreeNodes;

}
