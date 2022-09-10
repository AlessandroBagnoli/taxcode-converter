package com.github.alessandrobagnoli.taxcodeconverter.config;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import com.github.alessandrobagnoli.taxcodeconverter.utils.CityCSVLoader;
import com.github.alessandrobagnoli.taxcodeconverter.utils.CityCSVLoader.CityCSV;
import lombok.Builder;
import lombok.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AppConfig {

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

  @Value
  @Builder
  public static class Place {

    String cityName;
    String province;
  }

}
