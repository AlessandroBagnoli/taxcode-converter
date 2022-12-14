package com.github.alessandrobagnoli.taxcodeconverter.service;

import java.time.LocalDate;
import java.time.Month;
import java.time.Year;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.BinaryOperator;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import com.github.alessandrobagnoli.taxcodeconverter.config.AppConfig.Place;
import com.github.alessandrobagnoli.taxcodeconverter.dto.CalculatePersonDataResponse;
import com.github.alessandrobagnoli.taxcodeconverter.dto.CalculateTaxCodeRequest;
import com.github.alessandrobagnoli.taxcodeconverter.dto.Gender;
import com.github.alessandrobagnoli.taxcodeconverter.exception.CityNotPresentException;
import com.github.alessandrobagnoli.taxcodeconverter.utils.CityCSVLoader.CityCSV;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class TaxCodeCalculator {

  private static final Map<String, Integer> CHAR_MONTH_MAP = Map.ofEntries(
      Map.entry("A", 1),
      Map.entry("B", 2),
      Map.entry("C", 3),
      Map.entry("D", 4),
      Map.entry("E", 5),
      Map.entry("H", 6),
      Map.entry("L", 7),
      Map.entry("M", 8),
      Map.entry("P", 9),
      Map.entry("R", 10),
      Map.entry("S", 11),
      Map.entry("T", 12)
  );
  private static final Map<Integer, String> MONTH_CHAR_MAP = CHAR_MONTH_MAP.entrySet().stream()
      .collect(Collectors.toMap(Map.Entry::getValue, Map.Entry::getKey));
  private static final List<Character> VOWELS = List.of('A', 'E', 'I', 'O', 'U');
  private static final Map<Integer, String> CONTROL_CHARACTER_MAP = Map.ofEntries(
      Map.entry(0, "A"),
      Map.entry(1, "B"),
      Map.entry(2, "C"),
      Map.entry(3, "D"),
      Map.entry(4, "E"),
      Map.entry(5, "F"),
      Map.entry(6, "G"),
      Map.entry(7, "H"),
      Map.entry(8, "I"),
      Map.entry(9, "J"),
      Map.entry(10, "K"),
      Map.entry(11, "L"),
      Map.entry(12, "M"),
      Map.entry(13, "N"),
      Map.entry(14, "O"),
      Map.entry(15, "P"),
      Map.entry(16, "Q"),
      Map.entry(17, "R"),
      Map.entry(18, "S"),
      Map.entry(19, "T"),
      Map.entry(20, "U"),
      Map.entry(21, "V"),
      Map.entry(22, "W"),
      Map.entry(23, "X"),
      Map.entry(24, "Y"),
      Map.entry(25, "Z")
  );
  private static final Map<Character, Integer> EVEN_SUM_MAP = Map.ofEntries(
      Map.entry('0', 0),
      Map.entry('A', 0),
      Map.entry('1', 1),
      Map.entry('B', 1),
      Map.entry('2', 2),
      Map.entry('C', 2),
      Map.entry('3', 3),
      Map.entry('D', 3),
      Map.entry('4', 4),
      Map.entry('E', 4),
      Map.entry('5', 5),
      Map.entry('F', 5),
      Map.entry('6', 6),
      Map.entry('G', 6),
      Map.entry('7', 7),
      Map.entry('H', 7),
      Map.entry('8', 8),
      Map.entry('I', 8),
      Map.entry('9', 9),
      Map.entry('J', 9),
      Map.entry('K', 10),
      Map.entry('L', 11),
      Map.entry('M', 12),
      Map.entry('N', 13),
      Map.entry('O', 14),
      Map.entry('P', 15),
      Map.entry('Q', 16),
      Map.entry('R', 17),
      Map.entry('S', 18),
      Map.entry('T', 19),
      Map.entry('U', 20),
      Map.entry('V', 21),
      Map.entry('W', 22),
      Map.entry('X', 23),
      Map.entry('Y', 24),
      Map.entry('Z', 25)
  );
  private static final Map<Character, Integer> ODD_SUM_MAP = Map.ofEntries(
      Map.entry('0', 1),
      Map.entry('A', 1),
      Map.entry('1', 0),
      Map.entry('B', 0),
      Map.entry('2', 5),
      Map.entry('C', 5),
      Map.entry('3', 7),
      Map.entry('D', 7),
      Map.entry('4', 9),
      Map.entry('E', 9),
      Map.entry('5', 13),
      Map.entry('F', 13),
      Map.entry('6', 15),
      Map.entry('G', 15),
      Map.entry('7', 17),
      Map.entry('H', 17),
      Map.entry('8', 19),
      Map.entry('I', 19),
      Map.entry('9', 21),
      Map.entry('J', 21),
      Map.entry('K', 2),
      Map.entry('L', 4),
      Map.entry('M', 18),
      Map.entry('N', 20),
      Map.entry('O', 11),
      Map.entry('P', 3),
      Map.entry('Q', 6),
      Map.entry('R', 8),
      Map.entry('S', 12),
      Map.entry('T', 14),
      Map.entry('U', 16),
      Map.entry('V', 10),
      Map.entry('W', 22),
      Map.entry('X', 25),
      Map.entry('Y', 24),
      Map.entry('Z', 23)
  );

  private static final BinaryOperator<String> CASE_0 = (vowels, consonants) -> {
    if (vowels.length() > 2) {
      return vowels.substring(0, 3);
    }
    if (vowels.length() == 2) {
      return vowels.concat("X");
    }
    if (vowels.length() == 1) {
      return vowels.concat("XX");
    }
    return "XXX";
  };

  private static final BinaryOperator<String> CASE_1 = (vowels, consonants) -> {
    if (vowels.length() >= 2) {
      return consonants.concat(vowels.substring(0, 2));
    }
    if (vowels.length() == 1) {
      return consonants.concat(vowels).concat("X");
    }
    return consonants.concat("XX");
  };

  private static final BinaryOperator<String> CASE_2 = (vowels, consonants) -> {
    if (vowels.length() >= 2) {
      return consonants + vowels.charAt(0);
    }
    return consonants.concat("X");
  };

  private static final BinaryOperator<String> CASE_3 = (vowels, consonants) -> consonants;

  private static final BinaryOperator<String> SURNAME_CASE_DEFAULT = (vowels, consonants) -> consonants.substring(0, 3);

  private static final BinaryOperator<String> NAME_CASE_DEFAULT = (vowels, consonants) -> consonants.charAt(0)
      + consonants.substring(2, 4);

  private static final Map<Integer, BinaryOperator<String>> SURNAME_FUNCTION_MAP = Map.of(
      0, CASE_0,
      1, CASE_1,
      2, CASE_2
  );

  private static final Map<Integer, BinaryOperator<String>> NAME_FUNCTION_MAP = Map.of(
      0, CASE_0,
      1, CASE_1,
      2, CASE_2,
      3, CASE_3
  );


  private final Map<String, CityCSV> cityCodesCache;
  private final Map<Place, CityCSV> cityPlacesCache;

  public CalculatePersonDataResponse reverseTaxCode(String taxCode) {
    var surname = taxCode.substring(0, 3).toUpperCase();
    var name = taxCode.substring(3, 6).toUpperCase();

    // day
    var sDay = taxCode.substring(9, 11);
    var day = Integer.parseInt(sDay);
    var gender = day > 31 ? Gender.FEMALE : Gender.MALE;
    var dayToConsider = day > 31 ? day - 40 : day;

    // month
    var m = taxCode.substring(8, 9).toUpperCase();
    var mm = CHAR_MONTH_MAP.getOrDefault(m, 0);

    // year
    var thisYear = Integer.parseInt(Year.now().format(DateTimeFormatter.ofPattern("uu")));
    var yy = taxCode.substring(6, 8);
    var y = Integer.parseInt(yy);
    var theYear = y >= thisYear ? 1900 + y : 2000 + y;
    var birthDate = LocalDate.of(theYear, Month.of(mm), dayToConsider);

    // city
    var cityCode = taxCode.substring(11, 15);
    var city = Optional.ofNullable(cityCodesCache.get(cityCode))
        .orElseThrow(
            () -> new CityNotPresentException(String.format("The city with code %s does not exist", cityCode)));

    return CalculatePersonDataResponse.builder()
        .name(name)
        .surname(surname)
        .gender(gender)
        .dateOfBirth(birthDate)
        .birthPlace(city.getName().toUpperCase())
        .province(city.getProvince().toUpperCase())
        .taxCode(taxCode)
        .build();
  }

  public String calculateTaxCode(CalculateTaxCodeRequest calculateTaxCodeRequest) {
    var fiscalCode = new StringBuilder();
    var fcSurname = StringUtils.deleteWhitespace(calculateTaxCodeRequest.surname())
        .toUpperCase()
        .replaceAll("[^A-Z]", StringUtils.EMPTY);
    var fcName = StringUtils.deleteWhitespace(calculateTaxCodeRequest.name())
        .toUpperCase()
        .replaceAll("[^A-Z]", StringUtils.EMPTY);
    var fcBirthDate = DateTimeFormatter.ofPattern("dd-MM-yyyy").format(calculateTaxCodeRequest.dateOfBirth());

    // surname
    var consonantsSurname = consonants(fcSurname);
    var vowelsSurname = vowels(fcSurname);
    var consonantsSurnameLength = consonantsSurname.length();
    var surname = SURNAME_FUNCTION_MAP.getOrDefault(consonantsSurnameLength, SURNAME_CASE_DEFAULT)
        .apply(vowelsSurname, consonantsSurname);
    fiscalCode.append(surname);

    // name
    var consonantsName = consonants(fcName);
    var vowelsName = vowels(fcName);
    var consonantsNameLength = consonantsName.length();
    var name = NAME_FUNCTION_MAP.getOrDefault(consonantsNameLength, NAME_CASE_DEFAULT)
        .apply(vowelsName, consonantsName);
    fiscalCode.append(name);

    // year
    fiscalCode.append(fcBirthDate, 8, 10);

    // month
    var month = fcBirthDate.charAt(3) == '0' ?
        Integer.parseInt(fcBirthDate.substring(4, 5))
        : Integer.parseInt(fcBirthDate.substring(3, 5));
    fiscalCode.append(MONTH_CHAR_MAP.get(month));

    // day
    var day = Integer.parseInt(fcBirthDate.substring(0, 2));
    if (calculateTaxCodeRequest.gender() == Gender.MALE) {
      fiscalCode.append(day < 10 ? "0" + day : day);
    } else {
      day += 40;
      fiscalCode.append(day);
    }

    // birth city
    var cityName = calculateTaxCodeRequest.birthPlace();
    var province = calculateTaxCodeRequest.province();
    var place = Place.builder()
        .cityName(cityName.toUpperCase())
        .province(province.toUpperCase())
        .build();
    var city = Optional.ofNullable(cityPlacesCache.get(place))
        .orElseThrow(() -> new CityNotPresentException(
            String.format("The city %s and province %s do not exist", cityName, province)));
    fiscalCode.append(city.getCode());

    // control char
    var evenSum = IntStream.range(1, 14)
        .filter(value -> value % 2 == 1)
        .reduce(0, (a, b) -> a + EVEN_SUM_MAP.get(fiscalCode.charAt(b)));
    var oddSum = IntStream.range(0, 15)
        .filter(value -> value % 2 == 0)
        .reduce(0, (a, b) -> a + ODD_SUM_MAP.get(fiscalCode.charAt(b)));
    var controlInteger = (evenSum + oddSum) % 26;
    var controlCharacter = CONTROL_CHARACTER_MAP.get(controlInteger);
    fiscalCode.append(controlCharacter);

    return fiscalCode.toString().toUpperCase();
  }

  private String consonants(String word) {
    var consonants = new StringBuilder();
    word.chars()
        .mapToObj(c -> (char) c)
        .toList()
        .stream()
        .filter(character -> !VOWELS.contains(character))
        .forEach(consonants::append);
    return consonants.toString();
  }

  private String vowels(String word) {
    var vowels = new StringBuilder();
    word.chars()
        .mapToObj(c -> (char) c)
        .toList()
        .stream()
        .filter(VOWELS::contains)
        .forEach(vowels::append);
    return vowels.toString();
  }

}
