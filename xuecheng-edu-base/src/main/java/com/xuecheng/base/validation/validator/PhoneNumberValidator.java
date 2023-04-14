package com.xuecheng.base.validation.validator;

import com.xuecheng.base.validation.constraints.PhoneNumberConstraint;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

/**
 * @author Domenic
 * @Classname PhoneNumberValidator
 * @Description 手机号码校验器
 * @Created by Domenic
 */
public class PhoneNumberValidator implements ConstraintValidator<PhoneNumberConstraint, String> {

    @Override
    public void initialize(PhoneNumberConstraint contactNumber) {
    }

    /**
     * 电话号码可以为空，若不为空则必须符合正则表达式
     * @param phone 电话号码
     * @param cxt 上下文
     * @return 是否通过校验
     */
    @Override
    public boolean isValid(String phone, ConstraintValidatorContext cxt) {
        if (phone == null || phone.isEmpty())
            return true;
        else
            return phone.matches("^[1][3,4,5,7,8][0-9]{9}$");
    }

}
