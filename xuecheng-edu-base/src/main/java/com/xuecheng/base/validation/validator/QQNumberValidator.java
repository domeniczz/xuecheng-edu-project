package com.xuecheng.base.validation.validator;

import com.xuecheng.base.validation.constraints.QQNumberConstraint;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

/**
 * @author Domenic
 * @Classname QQNumberValidator
 * @Description TODO
 * @Date 4/13/2023 8:56 PM
 * @Created by Domenic
 */
public class QQNumberValidator implements ConstraintValidator<QQNumberConstraint, String> {

    @Override
    public void initialize(QQNumberConstraint contactNumber) {
    }

    /**
     * QQ 号可以为空，若不为空则必须符合正则表达式
     * @param qq QQ 号
     * @param cxt 上下文
     * @return 是否通过校验
     */
    @Override
    public boolean isValid(String qq, ConstraintValidatorContext cxt) {
        if (qq == null || qq.isEmpty()) return true;
        else return qq.matches("^[1-9][0-9]{4,10}$");
    }

}
