package com.github.alessandrobagnoli.taxcodeconverter.service;

import com.github.alessandrobagnoli.taxcodeconverter.dto.CalculatePersonDataRequest;
import com.github.alessandrobagnoli.taxcodeconverter.dto.CalculatePersonDataResponse;
import com.github.alessandrobagnoli.taxcodeconverter.dto.CalculateTaxCodeRequest;
import com.github.alessandrobagnoli.taxcodeconverter.dto.CalculateTaxCodeResponse;
import com.github.alessandrobagnoli.taxcodeconverter.utils.validators.ValidCalculatePersonDataRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

@Service
@Validated
@RequiredArgsConstructor
public class TaxCodeConverterService {

  private final TaxCodeCalculator taxCodeCalculator;

  public CalculatePersonDataResponse fromTaxCodeToPerson(
      @ValidCalculatePersonDataRequest CalculatePersonDataRequest calculatePersonDataRequest) {
    var taxCode = calculatePersonDataRequest.getTaxCode();
    return taxCodeCalculator.reverseTaxCode(taxCode);
  }

  public CalculateTaxCodeResponse fromPersonToTaxCode(CalculateTaxCodeRequest calculateTaxCodeRequest) {
    var taxCode = taxCodeCalculator.calculateTaxCode(calculateTaxCodeRequest);
    return CalculateTaxCodeResponse.builder()
        .taxCode(taxCode)
        .build();
  }
}
