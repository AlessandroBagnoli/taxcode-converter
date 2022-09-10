package com.github.alessandrobagnoli.taxcodeconverter.controller;

import com.github.alessandrobagnoli.taxcodeconverter.dto.PersonDTO;
import com.github.alessandrobagnoli.taxcodeconverter.dto.TaxCodeDTO;
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
  public PersonDTO calculatePersonFromTaxCode(@RequestBody TaxCodeDTO taxCodeDTO) {
    return taxCodeConverterService.fromTaxCodeToPerson(taxCodeDTO);
  }

  @PostMapping("taxcode:calculate-tax-code")
  public TaxCodeDTO calculateTaxCodeFromPerson(@RequestBody PersonDTO personDTO) {
    return taxCodeConverterService.fromPersonToTaxCode(personDTO);
  }

}
