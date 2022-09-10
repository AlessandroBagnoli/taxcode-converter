package com.github.alessandrobagnoli.taxcodeconverter.service;

import java.time.LocalDate;
import java.time.Month;
import java.time.Year;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import com.github.alessandrobagnoli.taxcodeconverter.config.AppConfig.Place;
import com.github.alessandrobagnoli.taxcodeconverter.dto.PersonDTO;
import com.github.alessandrobagnoli.taxcodeconverter.dto.PersonDTO.Gender;
import com.github.alessandrobagnoli.taxcodeconverter.utils.CityCSVLoader.CityCSV;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class TaxCodeCalculator {

  private final Map<String, CityCSV> cityCodesCache;
  private final Map<Place, CityCSV> cityPlacesCache;

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

  public PersonDTO reverseTaxCode(String taxCode) {
    var surname = taxCode.substring(0, 3);
    var name = taxCode.substring(3, 6);
    var gender = Gender.MALE;

    // day
    var sDay = taxCode.substring(9, 11);
    var day = Integer.parseInt(sDay);
    if (day > 31) {
      gender = Gender.FEMALE;
      day -= 40;
    }

    // month
    var m = taxCode.substring(8, 9).toUpperCase();
    var mm = CHAR_MONTH_MAP.getOrDefault(m, 0);

    // year
    int theYear;
    var thisYear = Integer.parseInt(Year.now().format(DateTimeFormatter.ofPattern("uu")));
    var yy = taxCode.substring(6, 8);
    var y = Integer.parseInt(yy);
    theYear = y >= thisYear ? 1900 + y : 2000 + y;
    var birthDate = LocalDate.of(theYear, Month.of(mm), day);

    // city
    var cityCode = taxCode.substring(11, 15);
    var city = cityCodesCache.get(cityCode);

    return PersonDTO.builder()
        .name(name)
        .surname(surname)
        .gender(gender)
        .dateOfBirth(birthDate)
        .birthPlace(city.getName())
        .province(city.getProvince())
        .taxCode(taxCode)
        .build();
  }

  public String calculateTaxCode(PersonDTO personDTO) {
    var fiscalCode = "";
    var fcSurname = personDTO.getSurname().replace(" ", "").toUpperCase();
    var fcName = personDTO.getName().replace(" ", "").toUpperCase();
    var fcBirthDate = DateTimeFormatter.ofPattern("dd/MM/yyyy").format(personDTO.getDateOfBirth());

    var consonants = consonants(fcSurname);
    var vowels = vowels(fcSurname);
    var consonantsLength = consonants.length();
    switch (consonantsLength) {
      case 0:
        if (vowels.length() > 2) {
          fiscalCode += vowels.substring(0, 3);
        } else if (vowels.length() == 2) {
          fiscalCode += vowels + "x";
        } else if (vowels.length() == 1) {
          fiscalCode += vowels + "xx";
        } else {
          fiscalCode += "xxx";
        }
        break;
      case 1:
        if (vowels.length() == 1) {
          fiscalCode += consonants + vowels + "x";
        } else {
          fiscalCode += consonants + vowels.substring(0, 2);
        }
        break;
      case 2:
        if (vowels.length() > 0) {
          fiscalCode += consonants + vowels.charAt(0);
        } else {
          fiscalCode += consonants + "x";
        }
        break;
      default:
        fiscalCode += consonants.substring(0, 3);
        break;
    }

    // name
    consonants = consonants(fcName);
    vowels = vowels(fcName);
    consonantsLength = consonants.length();
    switch (consonantsLength) {
      case 0:
        if (vowels.length() > 2) {
          fiscalCode += vowels.substring(0, 3);
        } else if (vowels.length() == 2) {
          fiscalCode += vowels + "x";
        } else if (vowels.length() == 1) {
          fiscalCode += vowels + "xx";
        } else {
          fiscalCode += "xxx";
        }
        break;
      case 1:
        if (vowels.length() == 1) {
          fiscalCode += consonants + vowels + "x";
        } else {
          fiscalCode += consonants + vowels.substring(0, 2);
        }
        break;
      case 2:
        if (vowels.length() > 0) {
          fiscalCode += consonants + vowels.charAt(0);
        } else {
          fiscalCode += consonants + "x";
        }
        break;
      case 3:
        fiscalCode += consonants;
        break;
      default:
        fiscalCode += consonants.charAt(0) + consonants.substring(2, 4);
        break;
    }

    // year
    fiscalCode += fcBirthDate.substring(8, 10);

    // month
    int month = fcBirthDate.charAt(3) == '0' ?
        Integer.parseInt(fcBirthDate.substring(4, 5))
        : Integer.parseInt(fcBirthDate.substring(3, 5));
    fiscalCode += MONTH_CHAR_MAP.get(month);

    // day
    var day = Integer.parseInt(fcBirthDate.substring(0, 2));
    if (personDTO.getGender() == Gender.MALE) {
      fiscalCode += day < 10 ? "0" + day : day;
    } else {
      day += 40;
      fiscalCode += Integer.toString(day);
    }

    // birth city
    var place = Place.builder()
        .cityName(personDTO.getBirthPlace())
        .province(personDTO.getProvince())
        .build();
    var city = cityPlacesCache.get(place);
    fiscalCode += city.getCode();

    // control char
    fiscalCode = fiscalCode.toUpperCase();
    int evenSum = 0;
    for (int i = 1; i <= 13; i += 2) {
      evenSum += EVEN_SUM_MAP.get(fiscalCode.charAt(i));
    }
    var finalFiscalCode = fiscalCode;
    var oddSum = IntStream.range(0, 15)
        .filter(value -> value % 2 == 0)
        .reduce(0, (a, b) -> a + ODD_SUM_MAP.get(finalFiscalCode.charAt(b)));
    var controlInteger = (evenSum + oddSum) % 26;
    var controlCharacter = CONTROL_CHARACTER_MAP.get(controlInteger);
    fiscalCode += controlCharacter;

    return fiscalCode.toUpperCase();
  }

  private String consonants(String word) {
    var consonants = new StringBuilder();
    word.chars()
        .mapToObj(c -> (char) c)
        .collect(Collectors.toList())
        .stream()
        .filter(character -> !VOWELS.contains(character))
        .forEach(consonants::append);
    return consonants.toString();
  }

  private String vowels(String word) {
    var vowels = new StringBuilder();
    word.chars()
        .mapToObj(c -> (char) c)
        .collect(Collectors.toList())
        .stream()
        .filter(VOWELS::contains)
        .forEach(vowels::append);
    return vowels.toString();
  }

}
