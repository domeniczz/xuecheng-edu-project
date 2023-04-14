package com.xuecheng.base.validation.constraints;

import com.xuecheng.base.validation.validator.QQNumberValidator;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author Domenic
 * @Classname QQNumberConstraint
 * @Description QQ 号码校验注解
 * @Date 4/13/2023 8:55 PM
 * @Created by Domenic
 */
@Documented
@Constraint(validatedBy = QQNumberValidator.class)
@Target({ ElementType.METHOD, ElementType.FIELD })
@Retention(RetentionPolicy.RUNTIME)
public @interface QQNumberConstraint {
    String message() default "Invalid qq number";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
