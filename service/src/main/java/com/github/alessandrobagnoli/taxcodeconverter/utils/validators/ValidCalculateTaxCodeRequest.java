package com.github.alessandrobagnoli.taxcodeconverter.utils.validators;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.github.alessandrobagnoli.taxcodeconverter.utils.validators.impl.CalculateTaxCodeRequestValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = CalculateTaxCodeRequestValidator.class)
@Documented
public @interface ValidCalculateTaxCodeRequest {

  String message() default "Invalid CalculateTaxCodeRequest";

  Class<?>[] groups() default {};

  Class<? extends Payload>[] payload() default {};
}