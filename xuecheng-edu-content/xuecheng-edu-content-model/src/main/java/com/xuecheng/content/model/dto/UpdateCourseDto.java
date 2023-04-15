package com.xuecheng.content.model.dto;

import com.xuecheng.base.validation.ValidationGroups;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import javax.validation.constraints.Min;

/**
 * @author Domenic
 * @Classname UpdateCourseDto
 * @Description 编辑课程的 DTO
 * @Created by Domenic
 */
@Data
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@ApiModel(value = "UpdateCourseDto", description = "编辑课程基本信息")
public class UpdateCourseDto extends AddCourseDto {

    @ApiModelProperty(value = "课程 ID", required = true)
    @Min(value = 0L, message = "课程 ID 不能小于 0", groups = { ValidationGroups.Update.class, ValidationGroups.Delete.class })
    private Long id;

}
