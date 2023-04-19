package com.xuecheng.base.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author Domenic
 * @Classname ResponseResult
 * @Description 响应结果
 * @Created by Domenic
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ResponseResult {

    private int statusCode;

    private String message;

}
