package com.github.alessandrobagnoli.taxcodeconverter.dto;

import java.time.LocalDate;

import lombok.Builder;
import lombok.Data;
import lombok.extern.jackson.Jacksonized;

@Data
@Builder
@Jacksonized
public class CalculatePersonDataResponse {

  private Gender gender;
  private String name;
  private String surname;
  private LocalDate dateOfBirth;
  private String birthPlace;
  private String province;
  private String taxCode;

}
