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
 * @Created by Domenic
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class PageParams {

    /**
     * <p>
     * 当前页码<br/>
     * 类型为 Long 是因为 Mybatis-Plus 接口的分页参数类型也是 Long
     * </p>
     */
    @ApiModelProperty("当前页码")
    private Long pageNo = 1L;

    /**
     * <p>
     * 每页最多显示记录数
     * </p>
     */
    @ApiModelProperty("每页最多显示记录数")
    private Long pageSize = 10L;

}
