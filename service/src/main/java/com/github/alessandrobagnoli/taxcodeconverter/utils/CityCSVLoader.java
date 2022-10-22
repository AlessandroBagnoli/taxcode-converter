package com.github.alessandrobagnoli.taxcodeconverter.utils;

import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.List;

import com.univocity.parsers.annotations.Parsed;
import com.univocity.parsers.common.processor.BeanListProcessor;
import com.univocity.parsers.csv.CsvParser;
import com.univocity.parsers.csv.CsvParserSettings;
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
    var beanListProcessor = new BeanListProcessor<>(CityCSV.class);
    var settings = new CsvParserSettings();
    settings.setHeaderExtractionEnabled(true);
    settings.setProcessor(beanListProcessor);
    settings.getFormat().setDelimiter(";");
    var parser = new CsvParser(settings);
    parser.parse(reader);
    var cities = beanListProcessor.getBeans();
    log.info("Loaded {} cities from csv file", cities.size());
    return cities;
  }

  @Data
  @Builder
  @NoArgsConstructor
  @AllArgsConstructor
  public static class CityCSV {

    @Parsed
    private String name;
    @Parsed
    private String province;
    @Parsed
    private String code;

  }


}
