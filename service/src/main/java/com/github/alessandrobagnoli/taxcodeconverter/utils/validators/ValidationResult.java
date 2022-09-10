package com.github.alessandrobagnoli.taxcodeconverter.utils.validators;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@AllArgsConstructor
public class ValidationResult {

  private List<ValidationDetail> details;

  public boolean isValid() {
    return details.isEmpty();
  }

  @Data
  @Builder
  public static class ValidationDetail {

    private String field;
    private String message;
  }
}