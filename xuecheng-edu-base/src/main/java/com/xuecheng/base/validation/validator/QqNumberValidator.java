package com.xuecheng.base.validation.validator;

import com.xuecheng.base.validation.constraints.QqNumberConstraint;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

/**
 * @author Domenic
 * @Classname QqNumberValidator
 * @Description QQ 号码校验器
 * @Created by Domenic
 */
public class QqNumberValidator implements ConstraintValidator<QqNumberConstraint, String> {

    @Override
    public void initialize(QqNumberConstraint contactNumber) {
    }

    /**
     * QQ 号可以为空，若不为空则必须符合正则表达式
     * @param qq QQ 号
     * @param cxt 上下文
     * @return 是否通过校验
     */
    @Override
    public boolean isValid(String qq, ConstraintValidatorContext cxt) {
        if (qq == null || qq.isEmpty()) {
            return true;
        } else {
            return qq.matches("^[1-9][0-9]{4,10}$");
        }
    }

}
