package com.github.alessandrobagnoli.taxcodeconverter.service;

import java.time.LocalDate;
import java.time.Month;
import java.time.Year;
import java.time.format.DateTimeFormatter;

import com.github.alessandrobagnoli.taxcodeconverter.dto.PersonDTO;
import com.github.alessandrobagnoli.taxcodeconverter.dto.PersonDTO.Gender;
import org.springframework.stereotype.Component;

@Component
public class TaxCodeCalculator {

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
    var mm = 0;
    var m = taxCode.substring(8, 9).toLowerCase().charAt(0);
    switch (m) {
      case 'a':
        mm = 1;
        break;
      case 'b':
        mm = 2;
        break;
      case 'c':
        mm = 3;
        break;
      case 'd':
        mm = 4;
        break;
      case 'e':
        mm = 5;
        break;
      case 'h':
        mm = 6;
        break;
      case 'l':
        mm = 7;
        break;
      case 'm':
        mm = 8;
        break;
      case 'p':
        mm = 9;
        break;
      case 'r':
        mm = 10;
        break;
      case 's':
        mm = 11;
        break;
      case 't':
        mm = 12;
        break;
    }

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

//    City city = cityRepository.findByCodeIgnoreCase(cityCode).orElseThrow(() -> new CityNotFoundException(cityCode));

    return PersonDTO.builder()
        .name(name)
        .surname(surname)
        .gender(gender)
        .dateOfBirth(birthDate)
        .birthPlace(cityCode)
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

    // NAME
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


    /* Year */
    fiscalCode += fcBirthDate.substring(8, 10);
    /* Month */
    int month;
    if (fcBirthDate.charAt(3) == '0') {
      month = Integer.parseInt(fcBirthDate.substring(4, 5));
    } else {
      month = Integer.parseInt(fcBirthDate.substring(3, 5));
    }
    switch (month) {
      case 1: {
        fiscalCode += "A";
        break;
      }
      case 2: {
        fiscalCode += "B";
        break;
      }
      case 3: {
        fiscalCode += "C";
        break;
      }
      case 4: {
        fiscalCode += "D";
        break;
      }
      case 5: {
        fiscalCode += "E";
        break;
      }
      case 6: {
        fiscalCode += "H";
        break;
      }
      case 7: {
        fiscalCode += "L";
        break;
      }
      case 8: {
        fiscalCode += "M";
        break;
      }
      case 9: {
        fiscalCode += "P";
        break;
      }
      case 10: {
        fiscalCode += "R";
        break;
      }
      case 11: {
        fiscalCode += "S";
        break;
      }
      case 12: {
        fiscalCode += "T";
        break;
      }
    }

    // day
    var day = Integer.parseInt(fcBirthDate.substring(0, 2));
    if (personDTO.getGender() == Gender.MALE) {
      fiscalCode += day < 10 ? "0" + day : day;
    } else {
      day += 40;
      fiscalCode += Integer.toString(day);
    }
    // birth city
    fiscalCode += personDTO.getBirthPlace();

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
    word = word.toLowerCase();
    var consonants = new StringBuilder();
    for (char character : word.toCharArray()) {
      if (character != 'a' && character != 'e' && character != 'i'
          && character != 'o' && character != 'u') {
        consonants.append(character);
      }
    }
    return consonants.toString();
  }

  private String vowels(String word) {
    word = word.toLowerCase();
    var vowels = new StringBuilder();
    for (char character : word.toCharArray()) {
      if (character == 'a' || character == 'e' || character == 'i'
          || character == 'o' || character == 'u') {
        vowels.append(character);
      }
    }
    return vowels.toString();
  }

}
