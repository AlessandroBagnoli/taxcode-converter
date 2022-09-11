package com.github.alessandrobagnoli.taxcodeconverter.controller;

import java.time.Clock;
import java.time.Instant;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.alessandrobagnoli.taxcodeconverter.controller.TaxCodeConverterControllerAdvice.ApiError;
import com.github.alessandrobagnoli.taxcodeconverter.dto.CalculatePersonDataRequest;
import com.github.alessandrobagnoli.taxcodeconverter.service.TaxCodeConverterService;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

@WebMvcTest
class WebLayerIT {

  @MockBean
  private TaxCodeConverterService taxCodeConverterService;

  @MockBean
  private Clock clock;

  @Autowired
  private ObjectMapper objectMapper;

  @Autowired
  private MockMvc mockMvc;

  @SneakyThrows
  @Test
  void controllerAdviceShouldHandleRuntimeExceptionCorrectly() {
    // given
    var taxCode = "BGNLSN93P19H294L";
    var input = CalculatePersonDataRequest.builder()
        .taxCode(taxCode)
        .build();
    var now = Instant.now();
    given(clock.instant()).willReturn(now);
    given(taxCodeConverterService.fromTaxCodeToPerson(input)).willThrow(new RuntimeException("dummyException"));

    // when
    var actual = mockMvc.perform(post("/api/v1/taxcode:calculate-person-data")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(input)))
        .andReturn()
        .getResponse();

    // then
    assertThat(actual.getStatus()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR.value());
    var expected = ApiError.builder()
        .status(HttpStatus.INTERNAL_SERVER_ERROR)
        .timestamp(now)
        .path("/api/v1/taxcode:calculate-person-data")
        .error("dummyException")
        .build();
    assertThat(actual.getContentAsString()).isEqualTo(objectMapper.writeValueAsString(expected));
  }
}
