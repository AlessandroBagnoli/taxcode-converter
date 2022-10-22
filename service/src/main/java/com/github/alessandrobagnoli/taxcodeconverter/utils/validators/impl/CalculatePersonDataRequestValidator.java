package com.github.alessandrobagnoli.taxcodeconverter.utils.validators.impl;

import java.util.ArrayList;

import com.github.alessandrobagnoli.taxcodeconverter.dto.CalculatePersonDataRequest;
import com.github.alessandrobagnoli.taxcodeconverter.utils.validators.RequestValidator;
import com.github.alessandrobagnoli.taxcodeconverter.utils.validators.ValidCalculatePersonDataRequest;
import com.github.alessandrobagnoli.taxcodeconverter.utils.validators.ValidationResult;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.apache.commons.lang3.StringUtils;

public class CalculatePersonDataRequestValidator implements
    ConstraintValidator<ValidCalculatePersonDataRequest, CalculatePersonDataRequest>, RequestValidator {

  private static final String TAX_CODE_REGEX = "^([A-Z]{6}[0-9LMNPQRSTUV]{2}[ABCDEHLMPRST][0-9LMNPQRSTUV]{2}[A-Z][0-9LMNPQRSTUV]{3}[A-Z])$|(\\d{11})$";

  @Override
  public boolean isValid(CalculatePersonDataRequest r, ConstraintValidatorContext ctx) {
    var vr = new ValidationResult(new ArrayList<>());

    test("taxCode", StringUtils::isNotBlank, r.taxCode(), REQUIRED_MSG, vr);
    test("taxCode", s -> s.matches(TAX_CODE_REGEX), r.taxCode(), INVALID_MSG, vr);

    setConstraintValidatorContext(ctx, vr);
    return vr.isValid();
  }
}