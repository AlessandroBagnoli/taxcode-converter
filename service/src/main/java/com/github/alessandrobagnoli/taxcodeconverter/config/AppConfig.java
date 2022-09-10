package com.github.alessandrobagnoli.taxcodeconverter.config;

import java.util.Map;

import com.github.alessandrobagnoli.taxcodeconverter.service.CityCSVLoader;
import com.github.alessandrobagnoli.taxcodeconverter.service.CityCSVLoader.CityCSV;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AppConfig {

  @Bean
  public CityCSVLoader cityCSVLoader() {
    return new CityCSVLoader();
  }

  @Bean
  public Map<String, CityCSV> cityCache(CityCSVLoader cityCSVLoader) {
    return cityCSVLoader.parseCities();
  }

}
