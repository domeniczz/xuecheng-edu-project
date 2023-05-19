package com.xuecheng.base.model;

import java.io.Serializable;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 * @author Domenic
 * @Classname PageResult
 * @Description 分页查询的响应数据模型类
 * @Created by Domenic
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class PageResult<T extends Serializable> implements Serializable {

    /**
     * 数据列表
     */
    private List<T> items;

    /**
     * <p>
     * 总记录数
     * </p>
     * <!-- 不要改为基本类型，维持包装类 Long -->
     */
    private Long counts;

    /**
     * <p>
     * 当前页码
     * </p>
     * <!-- 不要改为基本类型，维持包装类 Long -->
     */
    private Long page;

    /**
     * <p>
     * 每页记录数
     * </p>
     * <!-- 不要改为基本类型，维持包装类 Long -->
     */
    private Long pageSize;

}