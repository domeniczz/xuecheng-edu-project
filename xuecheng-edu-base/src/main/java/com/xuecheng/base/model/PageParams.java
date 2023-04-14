package com.xuecheng.base.model;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 * @author Domenic
 * @Classname PageParams
 * @Description 分页查询的参数模型类
 * @Date 4/7/2023 4:06 PM
 * @Created by Domenic
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class PageParams {

    // 类型为 Long 是因为 Mybatis-Plus 接口的分页参数类型也是 Long
    @ApiModelProperty("当前页码")
    private Long pageNo = 1L;

    @ApiModelProperty("每页最多显示记录数")
    private Long pageSize = 10L;

}
