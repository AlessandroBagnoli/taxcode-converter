package com.github.alessandrobagnoli.taxcodeconverter.dto;

import java.time.LocalDate;

import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.jackson.Jacksonized;

@Data
@Builder
@Jacksonized
public class PersonDTO {

  private Gender gender;
  private String name;
  private String surname;
  private LocalDate dateOfBirth;
  private String birthPlace;
  private String province;
  private String taxCode;

  @RequiredArgsConstructor
  @Getter
  public enum Gender {

    UNSPECIFIED(0, "unspecified"),
    MALE(1, "male"),
    FEMALE(2, "female");

    private final Integer value;
    private final String stringValue;

  }

}