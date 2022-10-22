package com.github.alessandrobagnoli.taxcodeconverter.utils.validators.impl;

import java.time.Clock;
import java.util.ArrayList;

import com.github.alessandrobagnoli.taxcodeconverter.dto.CalculateTaxCodeRequest;
import com.github.alessandrobagnoli.taxcodeconverter.dto.Gender;
import com.github.alessandrobagnoli.taxcodeconverter.utils.validators.RequestValidator;
import com.github.alessandrobagnoli.taxcodeconverter.utils.validators.ValidCalculateTaxCodeRequest;
import com.github.alessandrobagnoli.taxcodeconverter.utils.validators.ValidationResult;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;

@RequiredArgsConstructor
public class CalculateTaxCodeRequestValidator implements
    ConstraintValidator<ValidCalculateTaxCodeRequest, CalculateTaxCodeRequest>, RequestValidator {

  private final Clock clock;

  @Override
  public boolean isValid(CalculateTaxCodeRequest r, ConstraintValidatorContext ctx) {
    var vr = new ValidationResult(new ArrayList<>());

    test("name", StringUtils::isNotBlank, r.name(), REQUIRED_MSG, vr);
    test("surname", StringUtils::isNotBlank, r.surname(), REQUIRED_MSG, vr);
    test("birthPlace", StringUtils::isNotBlank, r.birthPlace(), REQUIRED_MSG, vr);
    test("province", StringUtils::isNotBlank, r.province(), REQUIRED_MSG, vr);
    test("dateOfBirth",
        localDate -> localDate != null && localDate.isBefore(clock.instant().atZone(clock.getZone()).toLocalDate()),
        r.dateOfBirth(), INVALID_DATE, vr);
    test("gender", gender -> gender != null && gender != Gender.UNSPECIFIED, r.gender(), INVALID_MSG, vr);

    setConstraintValidatorContext(ctx, vr);
    return vr.isValid();
  }
}
