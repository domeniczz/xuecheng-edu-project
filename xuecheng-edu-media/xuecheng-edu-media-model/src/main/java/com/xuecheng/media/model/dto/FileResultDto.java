package com.xuecheng.media.model.dto;

import com.xuecheng.media.model.po.MediaFile;
import io.swagger.annotations.ApiModel;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 * @author Domenic
 * @Classname FileResultDto
 * @Description 文件操作返回参数 DTO
 * @Created by Domenic
 */
@Data
@ToString
@EqualsAndHashCode(callSuper = true)
@ApiModel(value = "FileResultDto", description = "上传文件返回参数")
public class FileResultDto extends MediaFile {

}
