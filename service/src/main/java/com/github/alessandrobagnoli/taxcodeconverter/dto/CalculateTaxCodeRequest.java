package com.github.alessandrobagnoli.taxcodeconverter.dto;

import java.time.LocalDate;

import lombok.Builder;

@Builder
public record CalculateTaxCodeRequest(
    Gender gender,
    String name,
    String surname,
    LocalDate dateOfBirth,
    String birthPlace,
    String province) {

}
