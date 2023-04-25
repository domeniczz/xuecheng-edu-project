package com.xuecheng.system.model.po;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * @author Domenic
 * @Classname Dictionary
 * @Description 数据字典
 * @Created by Domenic
 */
@Data
@TableName("dictionary")
@ApiModel(value = "Dictionary", description = "数据字典")
public class Dictionary implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * ID 标识
     */
    @TableId(value = "id", type = IdType.AUTO)
    @ApiModelProperty("ID 标识")
    private Long id;

    /**
     * 数据字典名称
     */
    @ApiModelProperty("数据字典名称")
    private String name;

    /**
     * 数据字典代码
     */
    @ApiModelProperty("数据字典代码")
    private String code;

    /**
     * 数据字典项--JSON 格式
     * [{
     *   "sd_name": "低级",
     *   "sd_id": "200001",
     *   "sd_status": "1"
     * }, {
     *   "sd_name": "中级",
     *   "sd_id": "200002",
     *   "sd_status": "1"
     * }, {
     *   "sd_name": "高级",
     *   "sd_id": "200003",
     *   "sd_status": "1"
     * }]
     */
    @ApiModelProperty("数据字典项--JSON 格式")
    private String itemValues;

}
