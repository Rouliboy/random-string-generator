package com.total.unique.generator.service;

import com.total.unique.generator.entity.UniqueID;
import com.total.unique.generator.repository.UniqueIDRepository;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class UniqueIDUpdater {

  @Autowired
  private UniqueIDRepository uniqueIDRepository;

  @Autowired
  private AsyncUniqueIdUpdater asyncUniqueIdUpdater;

  public Set<String> generate(final long numberOfElements) {
    final Set<String> result = new HashSet<>();

    log.info("Generating {} unique id", numberOfElements);

    long start = System.currentTimeMillis();
    for (int i = 0; i < numberOfElements; ++i) {
      result.add(RandomStringUtils.randomAlphabetic(10));
    }
    long stop = System.currentTimeMillis();
    log.info("Took {}ms to generate {} elements", stop - start, numberOfElements);

    final Set<UniqueID> uniqueIds = result.stream().map(s -> new UniqueID(s)).collect(Collectors.toSet());

    log.info("Storing in datababse");
    start = System.currentTimeMillis();

    uniqueIDRepository.saveAll(uniqueIds);
    stop = System.currentTimeMillis();
    log.info("Took {}ms to store {} elements", stop - start, numberOfElements);
    return result;
  }

  public void batchUpdate(final int count) {

    final int batchSize = 100_000;
    log.info("Count={}", count);
    final int quotient = count / batchSize;
    final int reste = count % batchSize;

    final List<Integer> list = new ArrayList<>();

    for (int i = 0; i < quotient; ++i) {
      list.add(batchSize);
    }
    if (reste > 0) {
      list.add(reste);
    }

    log.info("Liste={}", list);
    final List<Future<Long>> results = new ArrayList<>();

    list.forEach(l -> {
      log.info("Updating ids");
      results.add(asyncUniqueIdUpdater.update(l));
    });
    results.forEach(r -> {
      try {
        log.info("Getting result");
        r.get();
      } catch (final InterruptedException e) {
        e.printStackTrace();
      } catch (final ExecutionException e) {
        e.printStackTrace();
      }
    });
  }
}
