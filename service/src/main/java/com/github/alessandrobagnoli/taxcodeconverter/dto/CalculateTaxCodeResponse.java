package com.github.alessandrobagnoli.taxcodeconverter.dto;

import lombok.Builder;

@Builder
public record CalculateTaxCodeResponse(
    String taxCode
) {

}
