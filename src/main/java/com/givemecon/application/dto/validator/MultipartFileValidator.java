package com.givemecon.application.dto.validator;

import com.givemecon.common.util.FileUtils;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.springframework.web.multipart.MultipartFile;

public class MultipartFileValidator implements ConstraintValidator<ValidFile, MultipartFile> {

    @Override
    public boolean isValid(MultipartFile file, ConstraintValidatorContext context) {
        return FileUtils.isFileValid(file);
    }
}