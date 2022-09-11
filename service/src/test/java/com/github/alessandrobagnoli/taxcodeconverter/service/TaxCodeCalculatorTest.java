package com.github.alessandrobagnoli.taxcodeconverter.service;

import java.time.LocalDate;
import java.util.Map;
import java.util.stream.Stream;

import com.github.alessandrobagnoli.taxcodeconverter.config.AppConfig.Place;
import com.github.alessandrobagnoli.taxcodeconverter.dto.CalculatePersonDataResponse;
import com.github.alessandrobagnoli.taxcodeconverter.dto.CalculateTaxCodeRequest;
import com.github.alessandrobagnoli.taxcodeconverter.dto.Gender;
import com.github.alessandrobagnoli.taxcodeconverter.exception.CityNotPresentException;
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
import static org.junit.jupiter.api.Assertions.assertThrows;
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

    @ParameterizedTest
    @ArgumentsSource(ReverseTaxCodeTestsArgumentProvider.class)
    void shouldSucceed(String input, String cityCode, CalculatePersonDataResponse expected) {
      // given
      given(cityCodesCache.get(cityCode)).willReturn(CityCSV.builder()
          .name(expected.getBirthPlace())
          .province(expected.getProvince())
          .build());

      // when
      var actual = underTest.reverseTaxCode(input);

      // then
      assertThat(actual).isEqualTo(expected);
    }

    @Test
    void shouldThrowExceptionWhenNoCityFound() {
      // given
      var input = "BGNLSN93P19H295L";
      given(cityCodesCache.get("H295")).willReturn(null);

      // when
      var actual = assertThrows(CityNotPresentException.class, () -> underTest.reverseTaxCode(input));

      // then
      assertThat(actual).hasMessage("The city with code H295 does not exist");
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

    @Test
    void shouldThrowExceptionWhenNoCityFound() {
      // given
      var input = CalculateTaxCodeRequest.builder()
          .gender(Gender.MALE)
          .birthPlace("fakeCity")
          .province("fakeProvince")
          .dateOfBirth(LocalDate.of(1993, 9, 19))
          .name("Alessandro")
          .surname("Bagnoli")
          .build();
      given(cityPlacesCache.get(Place.builder()
          .cityName("FAKECITY")
          .province("FAKEPROVINCE")
          .build())).willReturn(null);

      // when
      var actual = assertThrows(CityNotPresentException.class, () -> underTest.calculateTaxCode(input));

      // then
      assertThat(actual).hasMessage("The city fakeCity and province fakeProvince do not exist");
    }

  }

  static class ReverseTaxCodeTestsArgumentProvider implements ArgumentsProvider {

    @Override
    public Stream<Arguments> provideArguments(ExtensionContext extensionContext) {
      return Stream.of(
          Arguments.of(
              "BGNLSN93P19H294L",
              "H294",
              CalculatePersonDataResponse.builder()
                  .taxCode("BGNLSN93P19H294L")
                  .gender(Gender.MALE)
                  .birthPlace("RIMINI")
                  .province("RN")
                  .dateOfBirth(LocalDate.of(1993, 9, 19))
                  .name("LSN")
                  .surname("BGN")
                  .build()
          ),
          Arguments.of(
              "PTRRSL10R45G479I",
              "G479",
              CalculatePersonDataResponse.builder()
                  .taxCode("PTRRSL10R45G479I")
                  .gender(Gender.FEMALE)
                  .birthPlace("PESARO")
                  .province("PU")
                  .dateOfBirth(LocalDate.of(2010, 10, 5))
                  .name("RSL")
                  .surname("PTR")
                  .build()
          )
      );
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
              "RSSQQQ50R05G479X"),
          Arguments.of(CalculateTaxCodeRequest.builder()
                  .gender(Gender.MALE)
                  .birthPlace("miagliano")
                  .province("bi")
                  .dateOfBirth(LocalDate.of(1993, 9, 19))
                  .name("hfg4")
                  .surname("5555")
                  .build(),
              "F189",
              "XXXHFG93P19F189A")
      );
    }
  }


}