package com.github.alessandrobagnoli.taxcodeconverter.utils.validators.impl;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.ArrayList;

import com.github.alessandrobagnoli.taxcodeconverter.dto.CalculatePersonDataRequest;
import com.github.alessandrobagnoli.taxcodeconverter.utils.validators.RequestValidator;
import com.github.alessandrobagnoli.taxcodeconverter.utils.validators.ValidTaxCodeDTO;
import com.github.alessandrobagnoli.taxcodeconverter.utils.validators.ValidationResult;
import org.apache.commons.lang3.StringUtils;

public class TaxCodeDTOValidator implements
    ConstraintValidator<ValidTaxCodeDTO, CalculatePersonDataRequest>, RequestValidator {

  private static final String TAX_CODE_REGEX = "^([A-Z]{6}[0-9LMNPQRSTUV]{2}[ABCDEHLMPRST][0-9LMNPQRSTUV]{2}[A-Z][0-9LMNPQRSTUV]{3}[A-Z])$|(\\d{11})$";

  @Override
  public boolean isValid(CalculatePersonDataRequest r, ConstraintValidatorContext ctx) {
    var vr = new ValidationResult(new ArrayList<>());

    test("taxCode", StringUtils::isNotBlank, r.getTaxCode(), REQUIRED_MSG, vr);
    test("taxCode", s -> s.matches(TAX_CODE_REGEX), r.getTaxCode(), INVALID_MSG, vr);

    setConstraintValidatorContext(ctx, vr);
    return vr.isValid();
  }
}