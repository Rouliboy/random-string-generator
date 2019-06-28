package com.total.unique.generator.service;

import com.total.unique.generator.entity.UniqueID;
import com.total.unique.generator.repository.UniqueIDRepository;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Component
@Slf4j
public class UniqueIDUpdater {

    @Autowired
    private UniqueIDRepository uniqueIDRepository;

    @Autowired
    private JdbcTemplate jdbcTemplate;

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

    @Transactional
    public void batchUpdate(final int count) {

        List<String> uniqueIds = uniqueIDRepository.findUniqueIdsNotInStatus(UniqueID.Status.PROCESSED, new PageRequest(0, count));

        log.info("UNique ids = {}", uniqueIds);

        String query = "update UNIQUE_ID set status = 'PROCESSED' where unique_id = ?";
        jdbcTemplate.batchUpdate(query, new BatchPreparedStatementSetter() {
            @Override
            public void setValues(PreparedStatement preparedStatement, int i) throws SQLException {
                preparedStatement.setString(1, uniqueIds.get(i));
            }

            @Override
            public int getBatchSize() {
                return uniqueIds.size();
            }
        });
    }
}
