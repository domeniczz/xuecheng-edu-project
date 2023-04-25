package com.xuecheng.media.model.dto;

import com.xuecheng.media.model.po.MediaFiles;
import io.swagger.annotations.ApiModel;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 * @author Domenic
 * @Classname UploadFileResultDto
 * @Description 上传文件返回参数 DTO
 * @Created by Domenic
 */
@Data
@ToString
@EqualsAndHashCode(callSuper = true)
@ApiModel(value = "UploadFileResultDto", description = "上传文件返回参数")
public class UploadFileResultDto extends MediaFiles {

}
