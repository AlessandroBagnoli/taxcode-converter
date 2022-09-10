package com.github.alessandrobagnoli.taxcodeconverter.service;

import java.time.LocalDate;

import com.github.alessandrobagnoli.taxcodeconverter.dto.PersonDTO;
import com.github.alessandrobagnoli.taxcodeconverter.dto.PersonDTO.Gender;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
class TaxCodeCalculatorTest {

  @InjectMocks
  private TaxCodeCalculator underTest;

  @Nested
  class ReverseTaxCodeTests {

    @Test
    void shouldSucceed() {
      // given
      var input = "BGNLSN93P19H294L";

      // when
      var actual = underTest.reverseTaxCode(input);

      // then
      var expected = PersonDTO.builder()
          .taxCode(input)
          .gender(Gender.MALE)
          .birthPlace("H294")
          .dateOfBirth(LocalDate.of(1993, 9, 19))
          .name("LSN")
          .surname("BGN")
          .build();
      assertThat(actual).isEqualTo(expected);
    }
  }

  @Nested
  class CalculateTaxCodeTests {

    @Test
    void shouldSucceed() {
      // given
      var input = PersonDTO.builder()
          .gender(Gender.MALE)
          .birthPlace("H294")
          .dateOfBirth(LocalDate.of(1993, 9, 19))
          .name("Alessandro")
          .surname("Bagnoli")
          .build();

      // when
      var actual = underTest.calculateTaxCode(input);

      // then
      var expected = "BGNLSN93P19H294L";
      assertThat(actual).isEqualTo(expected);
    }
  }


}