package com.github.alessandrobagnoli.taxcodeconverter.controller;

import java.time.LocalDate;

import com.github.alessandrobagnoli.taxcodeconverter.dto.PersonDTO;
import com.github.alessandrobagnoli.taxcodeconverter.dto.PersonDTO.Gender;
import com.github.alessandrobagnoli.taxcodeconverter.dto.TaxCodeDTO;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class TaxCodeConverterControllerTestIT {

  @Autowired
  private TaxCodeConverterController underTest;

  @Nested
  class CalculatePersonFromTaxCodeTests {

    @Test
    void shouldSucceedAndReturnAsExpected() {
      // given
      var taxCode = "BGNLSN93P19H294L";
      var input = TaxCodeDTO.builder()
          .taxCode(taxCode)
          .build();

      // when
      var actual = underTest.calculatePersonFromTaxCode(input);

      // then
      var expected = PersonDTO.builder()
          .taxCode(taxCode)
          .gender(Gender.MALE)
          .birthPlace("H294")
          .dateOfBirth(LocalDate.of(1993, 9, 19))
          .name("LSN")
          .surname("BGN")
          .build();
      assertThat(actual).usingRecursiveComparison().isEqualTo(expected);
    }

  }

  @Nested
  class CalculateTaxCodeFromPersonTests {

    @Test
    void shouldSucceedAndReturnAsExpected() {
      // given
      var input = PersonDTO.builder()
          .gender(Gender.MALE)
          .birthPlace("H294")
          .dateOfBirth(LocalDate.of(1993, 9, 19))
          .name("Alessandro")
          .surname("Bagnoli")
          .build();

      // when
      var actual = underTest.calculateTaxCodeFromPerson(input);

      // then
      var expected = TaxCodeDTO.builder()
          .taxCode("BGNLSN93P19H294L")
          .build();
      assertThat(actual).usingRecursiveComparison().isEqualTo(expected);
    }

  }


}