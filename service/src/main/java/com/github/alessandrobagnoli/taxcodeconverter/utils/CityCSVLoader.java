package com.github.alessandrobagnoli.taxcodeconverter.utils;

import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.List;

import com.opencsv.bean.CsvBindByName;
import com.opencsv.bean.CsvToBeanBuilder;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ClassPathResource;

@Slf4j
public class CityCSVLoader {

  private static final String CSV_PATH = "cities/italian-cities.csv";

  @SneakyThrows
  public List<CityCSV> parseCities() {
    var reader = new InputStreamReader(new ClassPathResource(CSV_PATH).getInputStream(), StandardCharsets.UTF_8);
    var cities = new CsvToBeanBuilder<CityCSV>(reader)
        .withType(CityCSV.class)
        .withIgnoreLeadingWhiteSpace(true)
        .withIgnoreEmptyLine(true)
        .withSeparator(';')
        .build()
        .parse();
    log.info("Loaded {} cities from csv file", cities.size());
    return cities;
  }

  @Data
  @Builder
  @NoArgsConstructor
  @AllArgsConstructor
  public static class CityCSV {

    @CsvBindByName(required = true)
    private String name;
    @CsvBindByName(required = true)
    private String province;
    @CsvBindByName(required = true)
    private String code;

  }


}
