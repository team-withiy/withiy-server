package com.server.global.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Documented
@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = DateFormatValidator.class)
public @interface DateFormat {
    String message() default "날짜 형식이 맞지 않습니다.: {pattern}";

    String pattern() default "yyyy-MM";

    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
