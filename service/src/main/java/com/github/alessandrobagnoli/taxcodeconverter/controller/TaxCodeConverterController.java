package com.github.alessandrobagnoli.taxcodeconverter.controller;

import com.github.alessandrobagnoli.taxcodeconverter.dto.CalculatePersonDataRequest;
import com.github.alessandrobagnoli.taxcodeconverter.dto.CalculatePersonDataResponse;
import com.github.alessandrobagnoli.taxcodeconverter.dto.CalculateTaxCodeRequest;
import com.github.alessandrobagnoli.taxcodeconverter.dto.CalculateTaxCodeResponse;
import com.github.alessandrobagnoli.taxcodeconverter.service.TaxCodeConverterService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/api/v1/",
    consumes = MediaType.APPLICATION_JSON_VALUE,
    produces = MediaType.APPLICATION_JSON_VALUE)
@RequiredArgsConstructor
public class TaxCodeConverterController {

  private final TaxCodeConverterService taxCodeConverterService;

  @PostMapping("taxcode:calculate-person-data")
  public CalculatePersonDataResponse calculatePersonData(@RequestBody CalculatePersonDataRequest request) {
    return taxCodeConverterService.fromTaxCodeToPerson(request);
  }

  @PostMapping("taxcode:calculate-tax-code")
  public CalculateTaxCodeResponse calculateTaxCodeFromPerson(@RequestBody CalculateTaxCodeRequest request) {
    return taxCodeConverterService.fromPersonToTaxCode(request);
  }

}
