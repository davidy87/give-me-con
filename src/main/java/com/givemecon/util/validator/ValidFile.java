package com.givemecon.util.validator;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = MultipartFileValidator.class)
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidFile {

    String message() default "File must be in correct format";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}