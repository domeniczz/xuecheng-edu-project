package com.xuecheng.content.model.po;

import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import lombok.Data;

import java.io.Serializable;

/**
 * @author Domenic
 * @Classname CourseMarket
 * @Description 课程营销信息
 * @Created by Domenic
 */
@Data
@TableName("course_market")
@ApiModel(value = "CourseMarket", description = "课程营销信息")
public class CourseMarket implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * <p>
     * 主键，课程id
     * </p>
     * <!-- 不要改为基本类型，维持包装类 Long -->
     */
    private Long id;

    /**
     * 收费规则，对应数据字典
     */
    private String charge;

    /**
     * 现价
     */
    private Double price;

    /**
     * 原价
     */
    private Double originalPrice;

    /**
     * 咨询qq
     */
    private String qq;

    /**
     * 微信
     */
    private String wechat;

    /**
     * 电话
     */
    private String phone;

    /**
     * 有效期天数
     */
    private Integer validDays;

}
