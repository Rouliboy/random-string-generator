package com.total.unique.generator.service;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.total.unique.generator.entity.UniqueID;
import com.total.unique.generator.repository.UniqueIDRepository;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.stream.Collectors;

@Component
@Slf4j
public class UniqueIDUpdater {

    @Autowired
    private UniqueIDRepository uniqueIDRepository;

    @Autowired
    private AsyncUniqueIdUpdater asyncUniqueIdUpdater;

    public Set<String> generate(final long numberOfElements) {
        Set<String> result = new HashSet<>();

        log.info("Generating {} unique id", numberOfElements);

        long start = System.currentTimeMillis();
        for(int i = 0; i < numberOfElements; ++i) {
            result.add(RandomStringUtils.randomAlphabetic(10));
        }
        long stop = System.currentTimeMillis();
        log.info("Took {}ms to generate {} elements", stop - start, numberOfElements);

        Set<UniqueID> uniqueIds =result.stream().map(s -> new UniqueID(s)).collect(Collectors.toSet());

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
        int quotient = count / batchSize;
        int reste = count % batchSize;

        List<Integer> list = new ArrayList<>();

        for (int i = 0; i < quotient; ++i) {
            list.add(batchSize);
        }
        if (reste > 0) {
            list.add(reste);
        }

        log.info("Liste={}", list);
        List<Future<Long>> results = new ArrayList<>();

        list.forEach(l -> {
            log.info("Updating ids");
            results.add(asyncUniqueIdUpdater.update(l));
        });
        results.forEach(r -> {
            try {
                log.info("Getting result");
                r.get();
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            }
        });
    }
}
