package com.github.alessandrobagnoli.taxcodeconverter.service;

import java.time.LocalDate;
import java.util.Map;
import java.util.stream.Stream;

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
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.ArgumentsProvider;
import org.junit.jupiter.params.provider.ArgumentsSource;
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

    @ParameterizedTest
    @ArgumentsSource(CalculateTaxCodeTestsArgumentProvider.class)
    void shouldSucceed(CalculateTaxCodeRequest input, String cityCode, String expected) {
      // given
      var birthPlace = input.getBirthPlace().toUpperCase();
      var province = input.getProvince().toUpperCase();
      given(cityPlacesCache.get(Place.builder()
          .cityName(birthPlace)
          .province(province)
          .build()))
          .willReturn(CityCSV.builder()
              .name(birthPlace)
              .province(province)
              .code(cityCode)
              .build());

      // when
      var actual = underTest.calculateTaxCode(input);

      // then
      assertThat(actual).isEqualTo(expected);
    }

  }

  static class CalculateTaxCodeTestsArgumentProvider implements ArgumentsProvider {

    @Override
    public Stream<Arguments> provideArguments(ExtensionContext context) {
      return Stream.of(
          Arguments.of(CalculateTaxCodeRequest.builder()
                  .gender(Gender.MALE)
                  .birthPlace("Rimini")
                  .province("RN")
                  .dateOfBirth(LocalDate.of(1993, 9, 19))
                  .name("Alessandro")
                  .surname("Bagnoli")
                  .build(),
              "H294",
              "BGNLSN93P19H294L"),
          Arguments.of(CalculateTaxCodeRequest.builder()
                  .gender(Gender.FEMALE)
                  .birthPlace("Rimini")
                  .province("RN")
                  .dateOfBirth(LocalDate.of(1993, 9, 19))
                  .name("F")
                  .surname("F")
                  .build(),
              "H294",
              "FXXFXX93P59H294P"),
          Arguments.of(CalculateTaxCodeRequest.builder()
                  .gender(Gender.FEMALE)
                  .birthPlace("Rimini")
                  .province("RN")
                  .dateOfBirth(LocalDate.of(1993, 9, 19))
                  .name("A")
                  .surname("FF")
                  .build(),
              "H294",
              "FFXAXX93P59H294S"),
          Arguments.of(CalculateTaxCodeRequest.builder()
                  .gender(Gender.MALE)
                  .birthPlace("roma")
                  .province("rm")
                  .dateOfBirth(LocalDate.of(2010, 10, 19))
                  .name("A")
                  .surname("AIO")
                  .build(),
              "H501",
              "AIOAXX10R19H501O"),
          Arguments.of(CalculateTaxCodeRequest.builder()
                  .gender(Gender.MALE)
                  .birthPlace("roma")
                  .province("rm")
                  .dateOfBirth(LocalDate.of(2010, 10, 19))
                  .name("AA")
                  .surname("")
                  .build(),
              "H501",
              "XXXAAX10R19H501R"),
          Arguments.of(CalculateTaxCodeRequest.builder()
                  .gender(Gender.MALE)
                  .birthPlace("pesaro")
                  .province("pu")
                  .dateOfBirth(LocalDate.of(1950, 10, 19))
                  .name("bae")
                  .surname("ba")
                  .build(),
              "G479",
              "BAXBAE50R19G479N"),
          Arguments.of(CalculateTaxCodeRequest.builder()
                  .gender(Gender.MALE)
                  .birthPlace("pesaro")
                  .province("pu")
                  .dateOfBirth(LocalDate.of(1950, 10, 19))
                  .name("ba")
                  .surname("baed")
                  .build(),
              "G479",
              "BDABAX50R19G479L"),
          Arguments.of(CalculateTaxCodeRequest.builder()
                  .gender(Gender.MALE)
                  .birthPlace("pesaro")
                  .province("pu")
                  .dateOfBirth(LocalDate.of(1950, 10, 5))
                  .name("qqq")
                  .surname("rossi")
                  .build(),
              "G479",
              "RSSQQQ50R05G479X")
      );
    }
  }


}