package com.github.alessandrobagnoli.taxcodeconverter.service;

import java.time.LocalDate;
import java.util.Map;

import com.github.alessandrobagnoli.taxcodeconverter.config.AppConfig.Place;
import com.github.alessandrobagnoli.taxcodeconverter.dto.CalculatePersonDataResponse;
import com.github.alessandrobagnoli.taxcodeconverter.dto.CalculateTaxCodeRequest;
import com.github.alessandrobagnoli.taxcodeconverter.dto.Gender;
import com.github.alessandrobagnoli.taxcodeconverter.utils.CityCSVLoader.CityCSV;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verifyNoMoreInteractions;

@ExtendWith(MockitoExtension.class)
class TaxCodeCalculatorTest {

  @Mock
  private Map<String, CityCSV> cityCodesCache;
  @Mock
  private Map<Place, CityCSV> cityPlacesCache;

  private TaxCodeCalculator underTest;

  @BeforeEach
  void setUp() {
    // no usage of @InjectMocks here since Mockito randomly injects the wrong maps
    this.underTest = new TaxCodeCalculator(cityCodesCache, cityPlacesCache);
  }

  @AfterEach
  void tearDown() {
    verifyNoMoreInteractions(cityCodesCache, cityPlacesCache);
  }

  @Nested
  class ReverseTaxCodeTests {

    @Test
    void shouldSucceed() {
      // given
      var input = "BGNLSN93P19H294L";
      given(cityCodesCache.get("H294")).willReturn(CityCSV.builder()
          .name("Rimini")
          .province("RN")
          .code("H294L")
          .build());

      // when
      var actual = underTest.reverseTaxCode(input);

      // then
      var expected = CalculatePersonDataResponse.builder()
          .taxCode(input)
          .gender(Gender.MALE)
          .birthPlace("Rimini")
          .province("RN")
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
      var input = CalculateTaxCodeRequest.builder()
          .gender(Gender.MALE)
          .birthPlace("Rimini")
          .province("RN")
          .dateOfBirth(LocalDate.of(1993, 9, 19))
          .name("Alessandro")
          .surname("Bagnoli")
          .build();
      given(cityPlacesCache.get(Place.builder()
          .cityName("RIMINI")
          .province("RN")
          .build()))
          .willReturn(CityCSV.builder()
              .name("RIMINI")
              .province("RN")
              .code("H294")
              .build());

      // when
      var actual = underTest.calculateTaxCode(input);

      // then
      var expected = "BGNLSN93P19H294L";
      assertThat(actual).isEqualTo(expected);
    }
  }


}