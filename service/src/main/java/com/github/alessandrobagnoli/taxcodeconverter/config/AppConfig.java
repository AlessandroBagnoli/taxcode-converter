package com.github.alessandrobagnoli.taxcodeconverter.config;

import java.time.Clock;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import com.github.alessandrobagnoli.taxcodeconverter.utils.CityCSVLoader;
import com.github.alessandrobagnoli.taxcodeconverter.utils.CityCSVLoader.CityCSV;
import lombok.Builder;
import org.springframework.aot.hint.annotation.RegisterReflectionForBinding;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportRuntimeHints;

@Configuration
@ImportRuntimeHints(AppRuntimeHints.class)
// This is needed in order to let csv parsing lib work in native image
@RegisterReflectionForBinding({CityCSV.class})
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
