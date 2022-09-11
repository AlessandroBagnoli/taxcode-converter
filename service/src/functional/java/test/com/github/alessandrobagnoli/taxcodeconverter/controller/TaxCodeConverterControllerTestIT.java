package com.github.alessandrobagnoli.taxcodeconverter.controller;

import java.time.Clock;
import java.time.Instant;
import java.time.LocalDate;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.alessandrobagnoli.taxcodeconverter.controller.TaxCodeConverterControllerAdvice.ApiError;
import com.github.alessandrobagnoli.taxcodeconverter.dto.CalculatePersonDataRequest;
import com.github.alessandrobagnoli.taxcodeconverter.dto.CalculatePersonDataResponse;
import com.github.alessandrobagnoli.taxcodeconverter.dto.CalculateTaxCodeRequest;
import com.github.alessandrobagnoli.taxcodeconverter.dto.CalculateTaxCodeResponse;
import com.github.alessandrobagnoli.taxcodeconverter.dto.Gender;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static java.util.Collections.singletonList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

@SpringBootTest
@AutoConfigureMockMvc
class TaxCodeConverterControllerTestIT {

  @Autowired
  private MockMvc mockMvc;

  @MockBean
  private Clock clock;

  @Autowired
  private ObjectMapper objectMapper;

  @Nested
  class CalculatePersonFromTaxCodeTests {

    @SneakyThrows
    @Test
    void shouldSucceedAndReturnAsExpected() {
      // given
      var taxCode = "BGNLSN93P19H294L";
      var input = CalculatePersonDataRequest.builder()
          .taxCode(taxCode)
          .build();

      // when
      var actual = mockMvc.perform(post("/api/v1/taxcode:calculate-person-data")
              .contentType(MediaType.APPLICATION_JSON)
              .content(objectMapper.writeValueAsString(input)))
          .andReturn()
          .getResponse();

      // then
      assertThat(actual.getStatus()).isEqualTo(HttpStatus.OK.value());
      var expected = CalculatePersonDataResponse.builder()
          .taxCode(taxCode)
          .gender(Gender.MALE)
          .birthPlace("Rimini")
          .province("RN")
          .dateOfBirth(LocalDate.of(1993, 9, 19))
          .name("LSN")
          .surname("BGN")
          .build();
      assertThat(actual.getContentAsString()).isEqualTo(objectMapper.writeValueAsString(expected));
    }

    @SneakyThrows
    @Test
    void shouldFailWhenInvalidTaxCode() {
      // given
      var taxCode = "invalidTaxCode";
      var input = CalculatePersonDataRequest.builder()
          .taxCode(taxCode)
          .build();
      var now = Instant.now();
      given(clock.instant()).willReturn(now);

      // when
      var actual = mockMvc.perform(post("/api/v1/taxcode:calculate-person-data")
              .contentType(MediaType.APPLICATION_JSON)
              .content(objectMapper.writeValueAsString(input)))
          .andReturn()
          .getResponse();

      // then
      assertThat(actual.getStatus()).isEqualTo(HttpStatus.BAD_REQUEST.value());
      var expected = ApiError.builder()
          .status(HttpStatus.BAD_REQUEST)
          .timestamp(now)
          .errors(singletonList("Invalid TaxCodeDTO: invalid value for property taxCode"))
          .path("/api/v1/taxcode:calculate-person-data")
          .build();
      assertThat(actual.getContentAsString()).isEqualTo(objectMapper.writeValueAsString(expected));
    }

  }

  @Nested
  class CalculateTaxCodeFromPersonTests {

    @SneakyThrows
    @Test
    void shouldSucceedAndReturnAsExpected() {
      // given
      var input = CalculateTaxCodeRequest.builder()
          .gender(Gender.MALE)
          .birthPlace("Rimini")
          .province("RN")
          .dateOfBirth(LocalDate.of(1993, 9, 19))
          .name("Alessandro")
          .surname("Bagnoli")
          .build();

      // when
      var actual = mockMvc.perform(post("/api/v1/taxcode:calculate-tax-code")
              .contentType(MediaType.APPLICATION_JSON)
              .content(objectMapper.writeValueAsString(input)))
          .andReturn()
          .getResponse();

      // then
      assertThat(actual.getStatus()).isEqualTo(HttpStatus.OK.value());
      var expected = CalculateTaxCodeResponse.builder()
          .taxCode("BGNLSN93P19H294L")
          .build();
      assertThat(actual.getContentAsString()).isEqualTo(objectMapper.writeValueAsString(expected));
    }

  }


}