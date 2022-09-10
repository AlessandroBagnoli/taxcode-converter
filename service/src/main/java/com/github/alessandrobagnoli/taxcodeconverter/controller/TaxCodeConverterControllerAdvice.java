package com.github.alessandrobagnoli.taxcodeconverter.controller;

import javax.validation.ConstraintViolationException;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class TaxCodeConverterControllerAdvice {

  @ResponseStatus(code = HttpStatus.BAD_REQUEST)
  @ExceptionHandler(ConstraintViolationException.class)
  public String handle(ConstraintViolationException exception) {
    return exception.getMessage();
  }

}
