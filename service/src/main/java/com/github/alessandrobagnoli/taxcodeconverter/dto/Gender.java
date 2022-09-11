package com.github.alessandrobagnoli.taxcodeconverter.dto;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum Gender {

  UNSPECIFIED(0),
  MALE(1),
  FEMALE(2);

  private final Integer value;

}