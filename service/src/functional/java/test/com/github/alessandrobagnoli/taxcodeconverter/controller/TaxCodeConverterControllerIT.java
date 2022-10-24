package com.github.alessandrobagnoli.taxcodeconverter.controller;

import java.net.URI;
import java.time.Clock;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.databind.ObjectMapper;
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
import org.springframework.http.ProblemDetail;
import org.springframework.test.web.servlet.MockMvc;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

@SpringBootTest
@AutoConfigureMockMvc
class TaxCodeConverterControllerIT {

  private static final String TIMESTAMP_PROPERTY = "timestamp";

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
          .birthPlace("RIMINI")
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
      assertThat(actual.getContentType()).isEqualTo(MediaType.APPLICATION_PROBLEM_JSON_VALUE);
      var expected = ProblemDetail.forStatus(HttpStatus.BAD_REQUEST);
      expected.setInstance(URI.create("/api/v1/taxcode:calculate-person-data"));
      expected.setProperty(TIMESTAMP_PROPERTY, now);
      expected.setDetail("Invalid CalculatePersonDataRequest: invalid value for property taxCode");
      assertThat(actual.getContentAsString()).isEqualTo(objectMapper.writeValueAsString(expected));
    }

    @SneakyThrows
    @Test
    void shouldFailWhenNoCityFound() {
      // given
      var taxCode = "BGNLSN93P19H295L";
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
      assertThat(actual.getStatus()).isEqualTo(HttpStatus.NOT_FOUND.value());
      assertThat(actual.getContentType()).isEqualTo(MediaType.APPLICATION_PROBLEM_JSON_VALUE);
      var expected = ProblemDetail.forStatus(HttpStatus.NOT_FOUND);
      expected.setProperty(TIMESTAMP_PROPERTY, now);
      expected.setDetail("The city with code H295 does not exist");
      expected.setInstance(URI.create("/api/v1/taxcode:calculate-person-data"));
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
      var now = Instant.now();
      given(clock.instant()).willReturn(now);
      given(clock.getZone()).willReturn(ZoneOffset.UTC);

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

    @SneakyThrows
    @Test
    void shouldFailWhenInvalidRequest() {
      // given
      var input = CalculateTaxCodeRequest.builder()
          .build();
      var now = Instant.now();
      given(clock.instant()).willReturn(now);
      given(clock.getZone()).willReturn(ZoneOffset.UTC);

      // when
      var actual = mockMvc.perform(post("/api/v1/taxcode:calculate-tax-code")
              .contentType(MediaType.APPLICATION_JSON)
              .content(objectMapper.writeValueAsString(input)))
          .andReturn()
          .getResponse();

      // then
      assertThat(actual.getStatus()).isEqualTo(HttpStatus.BAD_REQUEST.value());
      assertThat(actual.getContentType()).isEqualTo(MediaType.APPLICATION_PROBLEM_JSON_VALUE);
      var expectedErrors = List.of("Invalid CalculateTaxCodeRequest: missing required property name",
          "Invalid CalculateTaxCodeRequest: invalid date for property dateOfBirth: it must be in the past",
          "Invalid CalculateTaxCodeRequest: missing required property birthPlace",
          "Invalid CalculateTaxCodeRequest: missing required property province",
          "Invalid CalculateTaxCodeRequest: missing required property surname",
          "Invalid CalculateTaxCodeRequest: invalid value for property gender");
      var actualResponseDeserialized = objectMapper.readValue(actual.getContentAsString(), ProblemDetail.class);
      assertThat(actualResponseDeserialized.getType()).isEqualTo(URI.create("about:blank"));
      assertThat(actualResponseDeserialized.getInstance()).isEqualTo(URI.create("/api/v1/taxcode:calculate-tax-code"));
      assertThat(actualResponseDeserialized.getStatus()).isEqualTo(HttpStatus.BAD_REQUEST.value());
      assertThat(actualResponseDeserialized.getProperties()).containsExactly(
          Map.entry(TIMESTAMP_PROPERTY, now.toString()));
      assertThat(actualResponseDeserialized.getTitle()).isEqualTo(HttpStatus.BAD_REQUEST.getReasonPhrase());
      assertThat(actualResponseDeserialized.getDetail()).isNotNull();
      assertThat(Arrays.stream(actualResponseDeserialized.getDetail().split(","))
          .toList()).containsExactlyInAnyOrderElementsOf(expectedErrors);
    }

    @SneakyThrows
    @Test
    void shouldFailWhenNoCityFound() {
      // given
      var input = CalculateTaxCodeRequest.builder()
          .gender(Gender.MALE)
          .birthPlace("fakeCity")
          .province("fakeProvince")
          .dateOfBirth(LocalDate.of(1993, 9, 19))
          .name("Alessandro")
          .surname("Bagnoli")
          .build();
      var now = Instant.now();
      given(clock.instant()).willReturn(now);
      given(clock.getZone()).willReturn(ZoneOffset.UTC);

      // when
      var actual = mockMvc.perform(post("/api/v1/taxcode:calculate-tax-code")
              .contentType(MediaType.APPLICATION_JSON)
              .content(objectMapper.writeValueAsString(input)))
          .andReturn()
          .getResponse();

      // then
      assertThat(actual.getStatus()).isEqualTo(HttpStatus.NOT_FOUND.value());
      assertThat(actual.getContentType()).isEqualTo(MediaType.APPLICATION_PROBLEM_JSON_VALUE);
      var expected = ProblemDetail.forStatus(HttpStatus.NOT_FOUND);
      expected.setProperty(TIMESTAMP_PROPERTY, now);
      expected.setDetail("The city fakeCity and province fakeProvince do not exist");
      expected.setInstance(URI.create("/api/v1/taxcode:calculate-tax-code"));
      assertThat(actual.getContentAsString()).isEqualTo(objectMapper.writeValueAsString(expected));
    }

  }


}