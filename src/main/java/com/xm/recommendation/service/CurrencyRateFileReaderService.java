package com.xm.recommendation.service;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;


@Slf4j
@Service
public class CurrencyRateFileReaderService {

    private static final String CURRENCY_RATE_LOCATION = "currencies";

    public List<Pair<String, InputStream>> getCurrencyValuesFileNames() {
        ClassLoader cl = this.getClass().getClassLoader();
        ResourcePatternResolver resolver = new PathMatchingResourcePatternResolver(cl);
        Resource[] resources = new Resource[0];
        try {
            resources = resolver.getResources( CURRENCY_RATE_LOCATION + "/*_values.csv");
        } catch (IOException e) {
            log.error("no crypto currency rates were found", e);
        }
        return Stream.of(resources)
                .map(resource -> Pair.of(resource.getFilename(), cl.getResourceAsStream(CURRENCY_RATE_LOCATION + "/" + resource.getFilename())))
                .collect(Collectors.toList());
    }
}
