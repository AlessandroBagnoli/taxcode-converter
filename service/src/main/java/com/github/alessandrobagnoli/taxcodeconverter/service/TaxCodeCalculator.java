package com.github.alessandrobagnoli.taxcodeconverter.service;

import java.time.LocalDate;
import java.time.Month;
import java.time.Year;
import java.time.format.DateTimeFormatter;
import java.util.Map;
import java.util.stream.Collectors;

import com.github.alessandrobagnoli.taxcodeconverter.config.AppConfig.Place;
import com.github.alessandrobagnoli.taxcodeconverter.dto.PersonDTO;
import com.github.alessandrobagnoli.taxcodeconverter.dto.PersonDTO.Gender;
import com.github.alessandrobagnoli.taxcodeconverter.service.CityCSVLoader.CityCSV;
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
    if (y >= thisYear) {
      theYear = 1900 + y;
    } else {
      theYear = 2000 + y;
    }
    var birthDate = LocalDate.of(theYear, Month.of(mm), day);

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
      switch (fiscalCode.charAt(i)) {
        case '0':
        case 'A': {
          evenSum += 0;
          break;
        }
        case '1':
        case 'B': {
          evenSum += 1;
          break;
        }
        case '2':
        case 'C': {
          evenSum += 2;
          break;
        }
        case '3':
        case 'D': {
          evenSum += 3;
          break;
        }
        case '4':
        case 'E': {
          evenSum += 4;
          break;
        }
        case '5':
        case 'F': {
          evenSum += 5;
          break;
        }
        case '6':
        case 'G': {
          evenSum += 6;
          break;
        }
        case '7':
        case 'H': {
          evenSum += 7;
          break;
        }
        case '8':
        case 'I': {
          evenSum += 8;
          break;
        }
        case '9':
        case 'J': {
          evenSum += 9;
          break;
        }
        case 'K': {
          evenSum += 10;
          break;
        }
        case 'L': {
          evenSum += 11;
          break;
        }
        case 'M': {
          evenSum += 12;
          break;
        }
        case 'N': {
          evenSum += 13;
          break;
        }
        case 'O': {
          evenSum += 14;
          break;
        }
        case 'P': {
          evenSum += 15;
          break;
        }
        case 'Q': {
          evenSum += 16;
          break;
        }
        case 'R': {
          evenSum += 17;
          break;
        }
        case 'S': {
          evenSum += 18;
          break;
        }
        case 'T': {
          evenSum += 19;
          break;
        }
        case 'U': {
          evenSum += 20;
          break;
        }
        case 'V': {
          evenSum += 21;
          break;
        }
        case 'W': {
          evenSum += 22;
          break;
        }
        case 'X': {
          evenSum += 23;
          break;
        }
        case 'Y': {
          evenSum += 24;
          break;
        }
        case 'Z': {
          evenSum += 25;
          break;
        }
      }
    }
    var oddSum = 0;
    for (int i = 0; i <= 14; i += 2) {
      switch (fiscalCode.charAt(i)) {
        case '0':
        case 'A': {
          oddSum += 1;
          break;
        }
        case '1':
        case 'B': {
          oddSum += 0;
          break;
        }
        case '2':
        case 'C': {
          oddSum += 5;
          break;
        }
        case '3':
        case 'D': {
          oddSum += 7;
          break;
        }
        case '4':
        case 'E': {
          oddSum += 9;
          break;
        }
        case '5':
        case 'F': {
          oddSum += 13;
          break;
        }
        case '6':
        case 'G': {
          oddSum += 15;
          break;
        }
        case '7':
        case 'H': {
          oddSum += 17;
          break;
        }
        case '8':
        case 'I': {
          oddSum += 19;
          break;
        }
        case '9':
        case 'J': {
          oddSum += 21;
          break;
        }
        case 'K': {
          oddSum += 2;
          break;
        }
        case 'L': {
          oddSum += 4;
          break;
        }
        case 'M': {
          oddSum += 18;
          break;
        }
        case 'N': {
          oddSum += 20;
          break;
        }
        case 'O': {
          oddSum += 11;
          break;
        }
        case 'P': {
          oddSum += 3;
          break;
        }
        case 'Q': {
          oddSum += 6;
          break;
        }
        case 'R': {
          oddSum += 8;
          break;
        }
        case 'S': {
          oddSum += 12;
          break;
        }
        case 'T': {
          oddSum += 14;
          break;
        }
        case 'U': {
          oddSum += 16;
          break;
        }
        case 'V': {
          oddSum += 10;
          break;
        }
        case 'W': {
          oddSum += 22;
          break;
        }
        case 'X': {
          oddSum += 25;
          break;
        }
        case 'Y': {
          oddSum += 24;
          break;
        }
        case 'Z': {
          oddSum += 23;
          break;
        }
      }
    }
    var controlInteger = (evenSum + oddSum) % 26;
    var controlCharacter = "";
    switch (controlInteger) {
      case 0: {
        controlCharacter = "A";
        break;
      }
      case 1: {
        controlCharacter = "B";
        break;
      }
      case 2: {
        controlCharacter = "C";
        break;
      }
      case 3: {
        controlCharacter = "D";
        break;
      }
      case 4: {
        controlCharacter = "E";
        break;
      }
      case 5: {
        controlCharacter = "F";
        break;
      }
      case 6: {
        controlCharacter = "G";
        break;
      }
      case 7: {
        controlCharacter = "H";
        break;
      }
      case 8: {
        controlCharacter = "I";
        break;
      }
      case 9: {
        controlCharacter = "J";
        break;
      }
      case 10: {
        controlCharacter = "K";
        break;
      }
      case 11: {
        controlCharacter = "L";
        break;
      }
      case 12: {
        controlCharacter = "M";
        break;
      }
      case 13: {
        controlCharacter = "N";
        break;
      }
      case 14: {
        controlCharacter = "O";
        break;
      }
      case 15: {
        controlCharacter = "P";
        break;
      }
      case 16: {
        controlCharacter = "Q";
        break;
      }
      case 17: {
        controlCharacter = "R";
        break;
      }
      case 18: {
        controlCharacter = "S";
        break;
      }
      case 19: {
        controlCharacter = "T";
        break;
      }
      case 20: {
        controlCharacter = "U";
        break;
      }
      case 21: {
        controlCharacter = "V";
        break;
      }
      case 22: {
        controlCharacter = "W";
        break;
      }
      case 23: {
        controlCharacter = "X";
        break;
      }
      case 24: {
        controlCharacter = "Y";
        break;
      }
      case 25: {
        controlCharacter = "Z";
        break;
      }
    }
    fiscalCode += controlCharacter;

    return fiscalCode.toUpperCase();
  }

  private String consonants(String word) {
    var consonants = new StringBuilder();
    for (char character : word.toCharArray()) {
      if (character != 'A' && character != 'E' && character != 'I'
          && character != 'O' && character != 'U') {
        consonants.append(character);
      }
    }
    return consonants.toString();
  }

  private String vowels(String word) {
    var vowels = new StringBuilder();
    for (char character : word.toCharArray()) {
      if (character == 'A' || character == 'E' || character == 'I'
          || character == 'O' || character == 'U') {
        vowels.append(character);
      }
    }
    return vowels.toString();
  }

}
