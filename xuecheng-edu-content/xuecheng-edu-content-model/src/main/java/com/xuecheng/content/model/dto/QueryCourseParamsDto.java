package com.xuecheng.content.model.dto;

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
public class QueryCourseParamsDto {

    @ApiModelProperty("审核状态")
    private String auditStatus;

    @ApiModelProperty("课程名称")
    private String courseName;

    @ApiModelProperty("发布状态")
    private String publishStatus;

}
