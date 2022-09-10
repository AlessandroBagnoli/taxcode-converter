package com.github.alessandrobagnoli.taxcodeconverter.service;

import java.time.LocalDate;
import java.util.Map;

import com.github.alessandrobagnoli.taxcodeconverter.dto.PersonDTO;
import com.github.alessandrobagnoli.taxcodeconverter.dto.PersonDTO.Gender;
import com.github.alessandrobagnoli.taxcodeconverter.service.CityCSVLoader.CityCSV;
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
class TaxCodeCalculatorTest {

  @Mock
  private Map<String, CityCSV> cityCache;

  @InjectMocks
  private TaxCodeCalculator underTest;

  @AfterEach
  void tearDown() {
    verifyNoMoreInteractions(cityCache);
  }

  @Nested
  class ReverseTaxCodeTests {

    @Test
    void shouldSucceed() {
      // given
      var input = "BGNLSN93P19H294L";
      given(cityCache.get("H294")).willReturn(CityCSV.builder()
          .name("Rimini")
          .build());

      // when
      var actual = underTest.reverseTaxCode(input);

      // then
      var expected = PersonDTO.builder()
          .taxCode(input)
          .gender(Gender.MALE)
          .birthPlace("Rimini")
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