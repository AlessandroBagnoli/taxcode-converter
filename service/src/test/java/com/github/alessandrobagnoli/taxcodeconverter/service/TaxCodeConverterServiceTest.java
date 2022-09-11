package com.github.alessandrobagnoli.taxcodeconverter.service;

import java.time.LocalDate;

import com.github.alessandrobagnoli.taxcodeconverter.dto.CalculatePersonDataRequest;
import com.github.alessandrobagnoli.taxcodeconverter.dto.CalculatePersonDataResponse;
import com.github.alessandrobagnoli.taxcodeconverter.dto.CalculateTaxCodeRequest;
import com.github.alessandrobagnoli.taxcodeconverter.dto.CalculateTaxCodeResponse;
import com.github.alessandrobagnoli.taxcodeconverter.dto.Gender;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verifyNoMoreInteractions;

@ExtendWith(MockitoExtension.class)
class TaxCodeConverterServiceTest {

  @Mock
  private TaxCodeCalculator taxCodeCalculator;

  @InjectMocks
  private TaxCodeConverterService underTest;

  @AfterEach
  void tearDown() {
    verifyNoMoreInteractions(taxCodeCalculator);
  }

  @Nested
  class FromTaxCodeToPersonTests {

    @Test
    void fromTaxCodeToPerson() {
      // given
      var input = CalculatePersonDataRequest.builder()
          .taxCode("BGNLSN93P19H294L")
          .build();
      var expected = CalculatePersonDataResponse.builder()
          .taxCode("BGNLSN93P19H294L")
          .gender(Gender.MALE)
          .birthPlace("H294")
          .dateOfBirth(LocalDate.of(1993, 9, 19))
          .name("LSN")
          .surname("BGN")
          .build();
      given(taxCodeCalculator.reverseTaxCode("BGNLSN93P19H294L")).willReturn(expected);

      // when
      var actual = underTest.fromTaxCodeToPerson(input);

      // then
      assertThat(actual).isEqualTo(expected);
    }

  }

  @Nested
  class FromPersonToTaxCodeTests {

    @Test
    void shouldSucceed() {
      // given
      var input = CalculateTaxCodeRequest.builder()
          .gender(Gender.MALE)
          .birthPlace("H294")
          .dateOfBirth(LocalDate.of(1993, 9, 19))
          .name("Alessandro")
          .surname("Bagnoli")
          .build();
      given(taxCodeCalculator.calculateTaxCode(input)).willReturn("BGNLSN93P19H294L");

      // when
      var actual = underTest.fromPersonToTaxCode(input);

      // then
      var expected = CalculateTaxCodeResponse.builder()
          .taxCode("BGNLSN93P19H294L")
          .build();
      assertThat(actual).isEqualTo(expected);
    }

  }

}