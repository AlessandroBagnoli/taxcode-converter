package com.github.alessandrobagnoli.taxcodeconverter.controller;

import java.time.LocalDate;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.alessandrobagnoli.taxcodeconverter.dto.PersonDTO;
import com.github.alessandrobagnoli.taxcodeconverter.dto.PersonDTO.Gender;
import com.github.alessandrobagnoli.taxcodeconverter.dto.TaxCodeDTO;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

@SpringBootTest
@AutoConfigureMockMvc
class TaxCodeConverterControllerTestIT {

  @Autowired
  private MockMvc mockMvc;

  @Autowired
  private ObjectMapper objectMapper;

  @Nested
  class CalculatePersonFromTaxCodeTests {

    @SneakyThrows
    @Test
    void shouldSucceedAndReturnAsExpected() {
      // given
      var taxCode = "BGNLSN93P19H294L";
      var input = TaxCodeDTO.builder()
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
      var expected = PersonDTO.builder()
          .taxCode(taxCode)
          .gender(Gender.MALE)
          .birthPlace("H294")
          .dateOfBirth(LocalDate.of(1993, 9, 19))
          .name("LSN")
          .surname("BGN")
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
      var input = PersonDTO.builder()
          .gender(Gender.MALE)
          .birthPlace("H294")
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
      var expected = TaxCodeDTO.builder()
          .taxCode("BGNLSN93P19H294L")
          .build();
      assertThat(actual.getContentAsString()).isEqualTo(objectMapper.writeValueAsString(expected));
    }

  }


}