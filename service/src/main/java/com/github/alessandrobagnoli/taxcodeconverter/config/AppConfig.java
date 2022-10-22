package com.github.alessandrobagnoli.taxcodeconverter.config;

import java.sql.Date;
import java.sql.Time;
import java.sql.Timestamp;
import java.time.Clock;
import java.util.Calendar;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import com.github.alessandrobagnoli.taxcodeconverter.controller.TaxCodeConverterControllerAdvice.ApiError;
import com.github.alessandrobagnoli.taxcodeconverter.dto.CalculatePersonDataRequest;
import com.github.alessandrobagnoli.taxcodeconverter.dto.CalculatePersonDataResponse;
import com.github.alessandrobagnoli.taxcodeconverter.dto.CalculateTaxCodeRequest;
import com.github.alessandrobagnoli.taxcodeconverter.dto.CalculateTaxCodeResponse;
import com.github.alessandrobagnoli.taxcodeconverter.dto.Gender;
import com.github.alessandrobagnoli.taxcodeconverter.utils.CityCSVLoader;
import com.github.alessandrobagnoli.taxcodeconverter.utils.CityCSVLoader.CityCSV;
import lombok.Builder;
import org.springframework.aot.hint.annotation.RegisterReflectionForBinding;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportRuntimeHints;

@Configuration
@ImportRuntimeHints(AppRuntimeHints.class)
@RegisterReflectionForBinding({CityCSV.class, CalculatePersonDataRequest.class, CalculatePersonDataResponse.class,
    CalculateTaxCodeRequest.class, CalculateTaxCodeResponse.class, Gender.class, ApiError.class, Calendar[].class,
    Date[].class, Time[].class, Timestamp[].class})
public class AppConfig {

  @Bean
  public Clock clock() {
    return Clock.systemUTC();
  }

  @Bean
  public List<CityCSV> cities() {
    return new CityCSVLoader().parseCities();
  }

  @Bean
  public Map<String, CityCSV> cityCodesCache(List<CityCSV> cities) {
    return cities.stream().collect(Collectors.toMap(CityCSV::getCode, Function.identity()));
  }

  @Bean
  public Map<Place, CityCSV> cityPlacesCache(List<CityCSV> cities) {
    return cities.stream().collect(Collectors.toMap(
        city -> Place.builder()
            .cityName(city.getName())
            .province(city.getProvince())
            .build(),
        Function.identity()));
  }

  @Builder
  public record Place(
      String cityName,
      String province) {

  }

}
