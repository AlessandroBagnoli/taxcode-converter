package com.github.alessandrobagnoli.taxcodeconverter.controller;

import java.time.Clock;

import com.github.alessandrobagnoli.taxcodeconverter.exception.CityNotPresentException;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

@RestControllerAdvice
@RequiredArgsConstructor
@Log4j2
public class TaxCodeConverterControllerAdvice {

  private static final String TIMESTAMP_PROPERTY = "timestamp";

  private final Clock clock;

  @ResponseStatus(code = HttpStatus.BAD_REQUEST)
  @ExceptionHandler(ConstraintViolationException.class)
  public ProblemDetail handle(ConstraintViolationException exception) {
    log.warn(exception);
    var problemDetail = ProblemDetail.forStatus(HttpStatus.BAD_REQUEST);
    problemDetail.setDetail(String.join(",", exception.getConstraintViolations().stream()
        .map(ConstraintViolation::getMessage)
        .toList()));
    problemDetail.setProperty(TIMESTAMP_PROPERTY, clock.instant());
    return problemDetail;
  }

  @ResponseStatus(code = HttpStatus.NOT_FOUND)
  @ExceptionHandler(CityNotPresentException.class)
  public ProblemDetail handle(CityNotPresentException exception, WebRequest webRequest) {
    log.warn(exception);
    var problemDetail = ProblemDetail.forStatus(HttpStatus.NOT_FOUND);
    problemDetail.setDetail(exception.getMessage());
    problemDetail.setProperty(TIMESTAMP_PROPERTY, clock.instant());
    return problemDetail;
  }

  @ResponseStatus(code = HttpStatus.INTERNAL_SERVER_ERROR)
  @ExceptionHandler(RuntimeException.class)
  public ProblemDetail handle(RuntimeException exception, WebRequest webRequest) {
    log.warn(exception);
    var problemDetail = ProblemDetail.forStatus(HttpStatus.INTERNAL_SERVER_ERROR);
    problemDetail.setDetail(exception.getMessage());
    problemDetail.setProperty(TIMESTAMP_PROPERTY, clock.instant());
    return problemDetail;
  }

}
