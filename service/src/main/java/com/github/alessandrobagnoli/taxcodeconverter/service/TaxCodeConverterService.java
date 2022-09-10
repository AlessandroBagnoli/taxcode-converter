package com.github.alessandrobagnoli.taxcodeconverter.service;

import com.github.alessandrobagnoli.taxcodeconverter.dto.PersonDTO;
import com.github.alessandrobagnoli.taxcodeconverter.dto.TaxCodeDTO;
import com.github.alessandrobagnoli.taxcodeconverter.utils.validators.ValidTaxCodeDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

@Service
@Validated
@RequiredArgsConstructor
public class TaxCodeConverterService {

  private final TaxCodeCalculator taxCodeCalculator;

  public PersonDTO fromTaxCodeToPerson(@ValidTaxCodeDTO TaxCodeDTO taxCodeDTO) {
    var taxCode = taxCodeDTO.getTaxCode();
    return taxCodeCalculator.reverseTaxCode(taxCode);
  }

  public TaxCodeDTO fromPersonToTaxCode(PersonDTO personDTO) {
    var taxCode = taxCodeCalculator.calculateTaxCode(personDTO);
    return TaxCodeDTO.builder()
        .taxCode(taxCode)
        .build();
  }
}
