package com.total.unique.generator.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class BeerDecimalGenerator {

  private static final int BASE_YEAR = 2019;

  private static final String VALUES = "0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";

  //@Cacheable
  public String from(final int year) {

    if (year < BASE_YEAR) {
      throw new IllegalArgumentException("Invalid year");
    }
    final int numberFromBaseYear = year - BASE_YEAR;
    final int length = VALUES.length();
    final int remain = numberFromBaseYear % length;
    final int quotient = numberFromBaseYear / length;

    final String result = VALUES.substring(quotient, quotient + 1) + VALUES.substring(remain, remain + 1);

    //log.info("{} | {} | {}", year, numberFromBaseYear, result);
    return result;
  }
}
