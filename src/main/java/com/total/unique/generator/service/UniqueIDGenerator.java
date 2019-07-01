package com.total.unique.generator.service;

import com.google.common.collect.Lists;
import com.total.unique.generator.entity.UniqueID;
import com.total.unique.generator.repository.UniqueIDRepository;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Calendar;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Component
@Slf4j
public class UniqueIDGenerator {

    @Autowired
    private UniqueIDRepository uniqueIDRepository;

    @Autowired
    private BeerDecimalGenerator beerDecimalGenerator;

    @Autowired
    private JdbcTemplate namedParameterJdbcTemplate;
    private int currentYear = 0;


    @PostConstruct
    public void init() {
        currentYear = Calendar.getInstance().get(Calendar.YEAR);
    }

    public Set<String> generate(final long numberOfElements) {
        Set<String> result = new HashSet<>();

        //log.info("Generating {} unique id", numberOfElements);

        long start = System.currentTimeMillis();
        for(int i = 0; i < numberOfElements; ++i) {
            result.add(beerDecimalGenerator.from(currentYear) + RandomStringUtils.randomAlphanumeric(8));
        }
        long stop = System.currentTimeMillis();
        //log.info(" > Took {}ms to generate {} elements", stop - start, numberOfElements);

        Set<UniqueID> uniqueIds =result.stream().map(s -> new UniqueID(s)).collect(Collectors.toSet());
        List<UniqueID> uniqueIdsList =result.stream().map(s -> new UniqueID(s)).collect(Collectors.toList());

        //log.info("Storing in datababse");
        start = System.currentTimeMillis();

        //uniqueIDRepository.saveAll(uniqueIds);
        saveBatch(uniqueIdsList);

        stop = System.currentTimeMillis();
       // log.info("< Took {}ms to store {} elements", stop - start, numberOfElements);
        return result;
    }

    public void saveBatch(final List<UniqueID> uniqueIdsList) {
        final int batchSize = 10000;

        List<List<UniqueID>> listOfLists = Lists.partition(uniqueIdsList, batchSize);

        String sql = "INSERT INTO unique_id_table(unique_id, status) VALUES (?, ?)";
        listOfLists.forEach(batchList -> {

            namedParameterJdbcTemplate.batchUpdate(sql,
                    new BatchPreparedStatementSetter() {
                        @Override
                        public void setValues(PreparedStatement ps, int i)
                                throws SQLException {
                            UniqueID employee = batchList.get(i);
                            ps.setString(1, employee.getUniqueId());
                            ps.setString(2, employee.getStatus().toString());
                        }

                        @Override
                        public int getBatchSize() {
                            return batchList.size();
                        }
                    });

        });

//        for (int j = 0; j < uniqueIdsList.size(); j += batchSize) {
//
//            final List<UniqueID> batchList = uniqueIdsList.subList(j, j + batchSize > uniqueIdsList.size() ? uniqueIdsList.size() : j + batchSize);
//
//            String sql = "INSERT INTO unique_id_table(unique_id, status) VALUES (?, ?)";
//            namedParameterJdbcTemplate.batchUpdate(sql,
//                    new BatchPreparedStatementSetter() {
//                        @Override
//                        public void setValues(PreparedStatement ps, int i)
//                                throws SQLException {
//                            UniqueID employee = batchList.get(i);
//                            ps.setString(1, employee.getUniqueId());
//                            ps.setString(2, employee.getStatus().toString());
//                        }
//
//                        @Override
//                        public int getBatchSize() {
//                            return batchList.size();
//                        }
//                    });
//
//        }
    }
}
