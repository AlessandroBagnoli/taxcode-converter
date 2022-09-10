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
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.context.request.WebRequest;

import static java.util.Collections.singletonList;

@RestControllerAdvice
@RequiredArgsConstructor
@Log4j2
public class TaxCodeConverterControllerAdvice {

  private final Clock clock;

  @ResponseStatus(code = HttpStatus.BAD_REQUEST)
  @ExceptionHandler(ConstraintViolationException.class)
  public ApiError handle(ConstraintViolationException exception, WebRequest webRequest) {
    log.warn(exception);
    return ApiError.builder()
        .timestamp(clock.instant())
        .status(HttpStatus.BAD_REQUEST)
        .errors(exception.getConstraintViolations().stream()
            .map(ConstraintViolation::getMessage)
            .collect(Collectors.toList()))
        .path(((ServletWebRequest) webRequest).getRequest().getRequestURI())
        .build();
  }

  @ResponseStatus(code = HttpStatus.INTERNAL_SERVER_ERROR)
  @ExceptionHandler(RuntimeException.class)
  public ApiError handle(RuntimeException exception, WebRequest webRequest) {
    log.warn(exception);
    return ApiError.builder()
        .timestamp(clock.instant())
        .status(HttpStatus.INTERNAL_SERVER_ERROR)
        .errors(singletonList(exception.getMessage()))
        .path(((ServletWebRequest) webRequest).getRequest().getRequestURI())
        .build();
  }

  @Value
  @Builder
  public static class ApiError {

    Instant timestamp;
    HttpStatus status;
    List<String> errors;
    String path;
  }

}
