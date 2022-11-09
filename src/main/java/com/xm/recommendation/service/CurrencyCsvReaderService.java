package com.xm.recommendation.service;

import com.xm.recommendation.model.CryptoCurrency;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.SortedSet;
import java.util.TimeZone;
import java.util.TreeSet;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class CurrencyCsvReaderService {

    private static final String EXPECTED_CSV_FILE_ENDING = "_values.csv";
    private final CurrencyRateFileReaderService currencyRateFileReaderService;

    public List<CryptoCurrency> readCurrencyValues() {
        return currencyRateFileReaderService.getCurrencyValuesFileNames()
                .stream()
                .map(resource -> {
                    BufferedReader reader = getReader(resource.getRight());
                    CSVParser parser = getCsvParser(reader, resource.getLeft());
                    if (parser != null) {
                        final SortedSet<CryptoCurrency.Rate> rates = parser.stream()
                                .map(this::getCurrencyRate)
                                .collect(Collectors.toCollection(() ->
                                        new TreeSet<>(Comparator.comparing(CryptoCurrency.Rate::getDate))));
                        return CryptoCurrency.builder()
                                .currencyName(getCurrencyNameFromFile(resource.getLeft()))
                                .values(rates)
                                .build();
                    }
                    return null;
                }).filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    private CryptoCurrency.Rate getCurrencyRate(CSVRecord row) {
        String timestamp = row.get(CryptoCurrency.Headers.TIMESTAMP.getHeader());
        String price = row.get(CryptoCurrency.Headers.PRICE.getHeader());
        return CryptoCurrency.Rate.builder()
                .price(new BigDecimal(price))
                .date(getDateByTimeStamp(timestamp))
                .build();
    }

    // assuming file name align with symbol column,
    // otherwise need to extend that method to take that into account 
    private String getCurrencyNameFromFile(String fileName) {
        if (fileName == null) {
            throw new IllegalStateException("resource cannot have null file name");
        }
        return fileName.substring(0, fileName.indexOf(EXPECTED_CSV_FILE_ENDING));
    }

    private LocalDateTime getDateByTimeStamp(String timestamp) {
        return LocalDateTime.ofInstant(Instant.ofEpochMilli(Long.parseLong(timestamp)),
                TimeZone.getTimeZone("UTC").toZoneId());
    }

    private BufferedReader getReader(InputStream is) {
        return new BufferedReader(new InputStreamReader(is));
    }

    private CSVParser getCsvParser(BufferedReader reader, String fileName) {
        try {
            return CSVFormat.DEFAULT.builder()
                    .setSkipHeaderRecord(true)
                    .setHeader(CryptoCurrency.Headers.TIMESTAMP.getHeader(),
                            CryptoCurrency.Headers.SYMBOL.getHeader(),
                            CryptoCurrency.Headers.PRICE.getHeader())
                    .build().parse(reader);
        } catch (IOException e) {
            log.error("could not process a csv file: {}", fileName);
            return null;
        }
    }
}
