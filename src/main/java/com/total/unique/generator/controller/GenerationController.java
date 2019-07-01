package com.total.unique.generator.controller;

import com.google.common.collect.ImmutableMap;
import com.total.unique.generator.entity.UniqueID;
import com.total.unique.generator.repository.UniqueIDRepository;
import com.total.unique.generator.service.UniqueIDGenerator;
import com.total.unique.generator.service.UniqueIdGeneratorLauncher;
import com.total.unique.generator.service.UniqueIDUpdater;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.Async;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("api/generate")
@Slf4j
public class GenerationController {

    @Autowired
    private UniqueIDGenerator generator;

    @Autowired
    private UniqueIDRepository uniqueIDRepository;

    @Autowired
    private UniqueIdGeneratorLauncher uniqueIdGeneratorLauncher;

    @Autowired
    private UniqueIDUpdater uniqueIDUpdater;

    @GetMapping("/{numberOfElements}")
    public void generate(@PathVariable final long numberOfElements) {
        generator.generate(numberOfElements);
    }

    @GetMapping("/count")
    public Map<String, Long> count() {
        return ImmutableMap.of("nbElements", uniqueIDRepository.count());
    }

    @GetMapping("/fetch/{count}")
    public ImmutableMap<String, Integer> fetch(@PathVariable final int count) {
        Pageable topTen = new PageRequest(0, count);

        long start = System.currentTimeMillis();
        List result = uniqueIDRepository.findByStatus(UniqueID.Status.NOT, topTen);
        long stop = System.currentTimeMillis();
        log.info("Took {}ms to fetch {} elements", stop - start, count);

        return ImmutableMap.of("nbElementsFetched", result.size());
    }

    @GetMapping("/batch")
    @Async
    public void batch(){
        uniqueIdGeneratorLauncher.load();
    }

    @GetMapping("/update/{count}")
    public void update(@PathVariable final int count) {
        uniqueIDUpdater.batchUpdate(count);
    }
}