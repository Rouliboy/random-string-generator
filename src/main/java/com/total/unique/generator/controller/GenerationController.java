package com.total.unique.generator.controller;

import com.google.common.collect.ImmutableMap;
import com.total.unique.generator.entity.UniqueID;
import com.total.unique.generator.repository.UniqueIDRepository;
import com.total.unique.generator.service.UniqueIDGenerator;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;
import java.util.Set;

@RestController
@RequestMapping("api/generate")
@Slf4j
public class GenerationController {

    @Autowired
    private UniqueIDGenerator generator;

    @Autowired
    private UniqueIDRepository uniqueIDRepository;



    @GetMapping("/{numberOfElements}")
    public Set<String> generate(@PathVariable final long numberOfElements) {
        return generator.generate(numberOfElements);
    }

    @GetMapping("/count")
    public Map<String, Long> generate() {
        return ImmutableMap.of("nbElemnts", uniqueIDRepository.count());
    }

    @GetMapping("/fetch/{count}")
    public List<UniqueID> fetch(@PathVariable final int count) {
        Pageable topTen = new PageRequest(0, count);

        long start = System.currentTimeMillis();
        List result = uniqueIDRepository.findByStatus(UniqueID.Status.NOT, topTen);
        long stop = System.currentTimeMillis();
        log.info("Took {}ms to fetch {} elements", stop - start, count);

        return result;
    }

}