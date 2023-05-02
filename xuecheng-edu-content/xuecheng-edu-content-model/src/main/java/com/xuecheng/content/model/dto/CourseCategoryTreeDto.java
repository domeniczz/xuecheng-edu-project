package com.xuecheng.content.model.dto;

import com.xuecheng.content.model.po.CourseCategory;
import io.swagger.annotations.ApiModel;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.util.List;

/**
 * @author Domenic
 * @Classname CourseCategoryTreeDto
 * @Description 课程分类的 DTO
 * @Created by Domenic
 */
@Data
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@ApiModel(value = "CourseCategoryTreeDto", description = "课程分类")
public class CourseCategoryTreeDto extends CourseCategory {

    private static final long serialVersionUID = 1L;

    /**
     * <p>
     * 子节点的 List<br/>
     * CourseCategory 是以树形结构存储的，因此需要存储子节点
     * </p>
     */
    private List<CourseCategory> childrenTreeNodes;

}
