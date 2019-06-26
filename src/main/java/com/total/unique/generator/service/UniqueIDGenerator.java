package com.total.unique.generator.service;

import com.total.unique.generator.entity.UniqueID;
import com.total.unique.generator.repository.UniqueIDRepository;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

@Component
@Slf4j
public class UniqueIDGenerator {

    @Autowired
    private UniqueIDRepository uniqueIDRepository;

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
}
