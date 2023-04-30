package com.xuecheng.base.validation.constraints;

import com.xuecheng.base.validation.validator.QqNumberValidator;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author Domenic
 * @Classname QqNumberConstraint
 * @Description QQ 号码校验注解
 * @Created by Domenic
 */
@Documented
@Constraint(validatedBy = QqNumberValidator.class)
@Target({ ElementType.METHOD, ElementType.FIELD })
@Retention(RetentionPolicy.RUNTIME)
public @interface QqNumberConstraint {
    String message() default "Invalid qq number";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
