package com.github.alessandrobagnoli.taxcodeconverter.service;

import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import com.opencsv.bean.CsvBindByName;
import com.opencsv.bean.CsvToBeanBuilder;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.core.io.ClassPathResource;

public class CityCSVLoader {

  private static final String CSV_PATH = "cities/italian-cities.csv";

  @SneakyThrows
  public Map<String, CityCSV> parseCities() {
    Reader reader = new InputStreamReader(new ClassPathResource(CSV_PATH).getInputStream(), StandardCharsets.UTF_8);
    List<CityCSV> csvToBean = new CsvToBeanBuilder<CityCSV>(reader)
        .withType(CityCSV.class)
        .withIgnoreLeadingWhiteSpace(true)
        .withIgnoreEmptyLine(true)
        .withSeparator(';')
        .build()
        .parse();

    return csvToBean.stream()
        .collect(Collectors.toMap(CityCSV::getCode, Function.identity()));
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
