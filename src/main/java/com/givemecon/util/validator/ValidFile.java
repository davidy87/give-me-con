package com.givemecon.util.validator;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = MultipartFileValidator.class)
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidFile {

    String message() default "{ValidFile}";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}