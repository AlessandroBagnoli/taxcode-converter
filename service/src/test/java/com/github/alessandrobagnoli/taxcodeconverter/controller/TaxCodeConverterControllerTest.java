package com.github.alessandrobagnoli.taxcodeconverter.controller;

import com.github.alessandrobagnoli.taxcodeconverter.dto.CalculatePersonDataRequest;
import com.github.alessandrobagnoli.taxcodeconverter.dto.CalculatePersonDataResponse;
import com.github.alessandrobagnoli.taxcodeconverter.dto.CalculateTaxCodeRequest;
import com.github.alessandrobagnoli.taxcodeconverter.dto.CalculateTaxCodeResponse;
import com.github.alessandrobagnoli.taxcodeconverter.service.TaxCodeConverterService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verifyNoMoreInteractions;

@ExtendWith(MockitoExtension.class)
class TaxCodeConverterControllerTest {

  @Mock
  private TaxCodeConverterService taxCodeConverterService;

  @InjectMocks
  private TaxCodeConverterController underTest;

  @AfterEach
  void tearDown() {
    verifyNoMoreInteractions(taxCodeConverterService);
  }

  @Test
  void calculatePersonFromTaxCodeShouldSucceed() {
    // given
    var input = CalculatePersonDataRequest.builder().build();
    var expected = CalculatePersonDataResponse.builder()
        .name("someName")
        .build();
    given(taxCodeConverterService.fromTaxCodeToPerson(input)).willReturn(expected);

    // when
    var actual = underTest.calculatePersonData(input);

    // then
    assertThat(actual).usingRecursiveComparison().isEqualTo(expected);
  }

  @Test
  void calculateTaxCodeFromPersonShouldSucceed() {
    // given
    var input = CalculateTaxCodeRequest.builder().build();
    var expected = CalculateTaxCodeResponse.builder()
        .taxCode("taxCode")
        .build();
    given(taxCodeConverterService.fromPersonToTaxCode(input)).willReturn(expected);

    // when
    var actual = underTest.calculateTaxCodeFromPerson(input);

    // then
    assertThat(actual).usingRecursiveComparison().isEqualTo(expected);
  }
}