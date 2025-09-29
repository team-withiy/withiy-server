package com.server.global.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class DateFormatValidator implements ConstraintValidator<DateFormat, String> {
    private String pattern;

    @Override
    public void initialize(DateFormat constraint) {
        this.pattern = constraint.pattern();
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        try {
            if ("yyyy-MM-dd".equals(pattern)) {
                LocalDate.parse(value, DateTimeFormatter.ofPattern(pattern));
            } else if ("yyyy-MM".equals(pattern)) {
                YearMonth.parse(value, DateTimeFormatter.ofPattern(pattern));
            } else {
                // 지원하지 않는 패턴일 경우 예외 처리
                throw new IllegalArgumentException("Unsupported date format pattern: " + pattern);
            }
            return true;
        } catch (DateTimeParseException exception) {
            log.error("에러", exception);
            throw new RuntimeException();
        }
    }
}
