package com.total.unique.generator.service;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;

@Slf4j
public class BeerDecimalGeneratorTest {

  private final BeerDecimalGenerator instance = new BeerDecimalGenerator();

  @Test
  public void test() {

    final List<String> list = new ArrayList<>();
    for (int year = 2019; year <= 3100; ++year) {
      list.add(instance.from(year));
    }

    final Set<String> set = new HashSet<>(list);

    log.info("Size equals = {}", set.size() == list.size());
  }
}
