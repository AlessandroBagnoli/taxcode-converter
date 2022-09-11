package com.github.alessandrobagnoli.taxcodeconverter.utils.validators;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.github.alessandrobagnoli.taxcodeconverter.utils.validators.impl.CalculatePersonDataRequestValidator;

@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = CalculatePersonDataRequestValidator.class)
@Documented
public @interface ValidCalculatePersonDataRequest {

  String message() default "Invalid CalculatePersonDataRequest";

  Class<?>[] groups() default {};

  Class<? extends Payload>[] payload() default {};
}