package com.xuecheng.media.model.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;

/**
 * @author Domenic
 * @Classname FileParamsDto
 * @Description 媒资文件操作请求参数 DTO
 * @Created by Domenic
 */
@Data
@ToString
@ApiModel(value = "FileParamsDto", description = "媒资文件操作请求参数")
public class FileParamsDto {

    /**
     * 文件名称
     */
    @ApiModelProperty("文件名称")
    private String filename;

    /**
     * 文件类型（文档，音频，视频）
     */
    @ApiModelProperty("文件类型")
    private String fileType;

    /**
     * 文件大小
     */
    @ApiModelProperty("文件大小")
    private Long fileSize;

    /**
     * 标签
     */
    @ApiModelProperty("标签")
    private String tags;

    /**
     * 上传人名称
     */
    @ApiModelProperty("上传人名称")
    private String username;

    /**
     * 备注
     */
    @ApiModelProperty("备注")
    private String remark;

}
