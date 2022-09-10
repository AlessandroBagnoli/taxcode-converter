package com.github.alessandrobagnoli.taxcodeconverter.controller;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import java.time.Clock;
import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;

import lombok.Builder;
import lombok.RequiredArgsConstructor;
import lombok.Value;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.context.request.WebRequest;

@RestControllerAdvice
@RequiredArgsConstructor
public class TaxCodeConverterControllerAdvice {

  private final Clock clock;

  @ResponseStatus(code = HttpStatus.BAD_REQUEST)
  @ExceptionHandler(ConstraintViolationException.class)
  public ApiErrorSchema handle(ConstraintViolationException exception, WebRequest webRequest) {
    return ApiErrorSchema.builder()
        .timestamp(clock.instant())
        .status(HttpStatus.BAD_REQUEST)
        .errors(exception.getConstraintViolations().stream()
            .map(ConstraintViolation::getMessage)
            .collect(Collectors.toList()))
        .path(((ServletWebRequest) webRequest).getRequest().getRequestURI())
        .build();
  }

  @Value
  @Builder
  public static class ApiErrorSchema {

    Instant timestamp;
    HttpStatus status;
    List<String> errors;
    String path;
  }

}
