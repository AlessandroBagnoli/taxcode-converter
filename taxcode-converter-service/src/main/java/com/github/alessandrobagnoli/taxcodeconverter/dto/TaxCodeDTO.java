package com.github.alessandrobagnoli.taxcodeconverter.dto;

import lombok.Builder;
import lombok.Data;
import lombok.extern.jackson.Jacksonized;

@Data
@Builder
@Jacksonized
public class TaxCodeDTO {

  private String taxCode;

}
