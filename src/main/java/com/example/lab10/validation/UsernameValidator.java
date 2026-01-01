package com.example.lab10.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.util.regex.Pattern;

public class UsernameValidator implements ConstraintValidator<ValidUsername, String> {
    private static final Pattern PATTERN = Pattern.compile("^[A-Za-z][A-Za-z0-9_]*$");

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (value == null) {
            return true; // handled by @NotBlank elsewhere
        }
        return PATTERN.matcher(value).matches();
    }
}
