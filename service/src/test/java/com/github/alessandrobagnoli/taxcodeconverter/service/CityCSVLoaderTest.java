package com.github.alessandrobagnoli.taxcodeconverter.service;

import com.github.alessandrobagnoli.taxcodeconverter.service.CityCSVLoader.CityCSV;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
class CityCSVLoaderTest {

  @InjectMocks
  private CityCSVLoader underTest;

  @Test
  void parseCitiesShouldSucceed() {
    // given

    // when
    var actual = underTest.parseCities();

    // then
    assertThat(actual).hasSize(7904)
        .contains(CityCSV.builder()
            .code("H294")
            .name("Rimini")
            .province("RN")
            .build());
  }
}