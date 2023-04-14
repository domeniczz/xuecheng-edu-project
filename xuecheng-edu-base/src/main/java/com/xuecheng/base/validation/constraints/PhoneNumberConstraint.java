package com.xuecheng.base.validation.constraints;

import com.xuecheng.base.validation.validator.PhoneNumberValidator;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author Domenic
 * @Classname PhoneNumberConstraint
 * @Description 手机号码校验注解
 * @Created by Domenic
 */
@Documented
@Constraint(validatedBy = PhoneNumberValidator.class)
@Target({ ElementType.METHOD, ElementType.FIELD })
@Retention(RetentionPolicy.RUNTIME)
public @interface PhoneNumberConstraint {
    String message() default "Invalid phone number";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
