package com.github.alessandrobagnoli.taxcodeconverter.utils.validators;

import javax.validation.ConstraintValidatorContext;
import java.util.List;
import java.util.function.Predicate;
import java.util.regex.Pattern;

import com.github.alessandrobagnoli.taxcodeconverter.utils.validators.ValidationResult.ValidationDetail;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.validator.constraintvalidation.HibernateConstraintValidatorContext;

public interface RequestValidator {

  String REQUIRED_MSG = "missing required property %s";
  String INVALID_MSG = "invalid value for property %s";
  String LONG_MSG = "input exceeds max chars for property %s";
  String DATE_FORMAT_MSG = "Unexpected date format - should be yyyy-MM-dd for property %s";
  String AGE_MSG = "should be at least 18 years old (based on property %s)";
  String EMAIL_FORMAT_MSG = "email format invalid for property %s";
  String COUNTRY_CODE_MESSAGE = "nationality code invalid for property %s";

  // Adds a ConstraintViolation (containing details about the errors) to the ConstraintValidatorContext
  // The violations will be processed in the ExceptionHandlerInterceptor
  default void setConstraintValidatorContext(ConstraintValidatorContext context, ValidationResult result) {
    if (!result.isValid() && !result.getDetails().isEmpty()) {
      var defaultMessage = context.getDefaultConstraintMessageTemplate() + ": ";
      context.disableDefaultConstraintViolation(); // Disabling the creation of default ConstraintViolation

      result.getDetails().forEach(detail -> context.unwrap(HibernateConstraintValidatorContext.class)
          .addMessageParameter("field", detail.getField())
          .addMessageParameter("error", "BAD_REQUEST")
          .buildConstraintViolationWithTemplate(defaultMessage + detail.getMessage())
          .addConstraintViolation()
      );
    }
  }

  /**
   * @param fName   - name of the field to be tested
   * @param isValid - a predicate where if .test() == true => the field is valid
   * @param field   - a field to be tested
   * @param msg     - an error msg that should have "%s" so that String.format can be used with the `fieldName`
   * @param result  - a validation result to be updated if the field is not valid
   * @param <T>     - a type of the field to be tested
   */
  default <T> void test(String fName, Predicate<T> isValid, T field, String msg, ValidationResult result) {
    if (!isValid.test(field)) {
      result.getDetails().add(ValidationDetail.builder().field(fName).message(String.format(msg, fName)).build());
    }
  }

  default <T> void testIfPresent(Predicate<T> isPresent, String fName, Predicate<T> isValid, T field, String msg,
      ValidationResult vr) {
    if (isPresent.test(field)) {
      test(fName, isValid, field, msg, vr);
    }
  }

  default <T> void testConditional(boolean condition, String fName, Predicate<T> isValid, T field, String msg,
      ValidationResult vr) {
    if (condition) {
      test(fName, isValid, field, msg, vr);
    }
  }

  /* Predicates Helpers */
  default Predicate<List<Integer>> enumsTest() {
    return c -> c.stream().allMatch(v -> v > 0);
  }

  default Predicate<String> dateTest() {
    return v -> Pattern.matches("^\\d{4}\\-(0?[1-9]|1[012])\\-(0?[1-9]|[12][0-9]|3[01])$", v);
  }

  // This was only required for combining Predicates
  default Predicate<String> nonBlankTest() {
    return StringUtils::isNotBlank;
  }
}