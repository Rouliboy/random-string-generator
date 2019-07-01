package com.total.unique.generator.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class UniqueIdGeneratorLauncher {

  @Autowired
  private UniqueIDGenerator uniqueIDGenerator;

  private static final int NB_ELEMENTS = 10_000;
  private static final int NB_INSERTS = 30_000;

  public void load() {

    int nbErrors = 0;

    final long globalStart = System.currentTimeMillis();

    for (int i = 1; i <= NB_INSERTS; ++i) {
      log.info("Loop {} ", i);
      final long start = System.currentTimeMillis();
      try {
        uniqueIDGenerator.generate(NB_ELEMENTS);
        final long stop = System.currentTimeMillis();
        log.info("Loop {} took {}ms to store {} elements", i, stop - start, NB_ELEMENTS);
      } catch (final DuplicateKeyException e) {
        log.error("ERROR : " + e.getMessage());
        nbErrors++;
      }
    }
    final long globalStop = System.currentTimeMillis();
    log.info("Took {}s to process {} elements", (globalStop - globalStart) / 1000,
        NB_INSERTS * NB_ELEMENTS);
    log.info("Nb errors during load of {} elements: {}", NB_INSERTS * NB_ELEMENTS, nbErrors);

  }
}
