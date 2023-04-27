package com.xuecheng.media.model.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;

/**
 * @author Domenic
 * @Classname QueryMediaParamsDto
 * @Description 媒资管理请求参数 DTO
 * @Created by Domenic
 */
@Data
@ToString
@ApiModel(value = "QueryMediaParamsDto", description = "媒资管理请求参数")
public class QueryMediaParamsDto {

    /**
     * 媒资文件名称
     */
    @ApiModelProperty("媒资文件名称")
    private String filename;

    /**
     * 媒资文件类型
     */
    @ApiModelProperty("媒资类型")
    private String fileType;

    /**
     * 审核状态
     */
    @ApiModelProperty("审核状态")
    private String auditStatus;

}
