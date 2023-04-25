package com.xuecheng.content.model.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 * @author Domenic
 * @Classname QueryCourseParamsDto
 * @Description 请求课程的参数模型类
 * @Created by Domenic
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
@ApiModel(value = "QueryCourseParamsDto", description = "请求课程的参数模型类")
public class QueryCourseParamsDto {

    /**
     * 审核状态
     */
    @ApiModelProperty("审核状态")
    private String auditStatus;

    /**
     * 课程名称
     */
    @ApiModelProperty("课程名称")
    private String courseName;

    /**
     * 发布状态
     */
    @ApiModelProperty("发布状态")
    private String publishStatus;

}
