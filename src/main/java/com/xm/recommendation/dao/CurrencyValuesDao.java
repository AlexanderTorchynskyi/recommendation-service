package com.xm.recommendation.dao;

import com.xm.recommendation.model.CryptoCurrency;
import com.xm.recommendation.model.ExtendedCurrency;
import com.xm.recommendation.service.CurrencyCsvReaderService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

/**
 *  That service is made to keep the data that was pulled from files.
 *  It reads files only during the start up and loads it into memory. 
 */
@Service
@RequiredArgsConstructor
public class CurrencyValuesDao {

    private final CurrencyCsvReaderService currencyCsvReaderService;
    private List<CryptoCurrency> currencies;
    private List<ExtendedCurrency> extendedCurrencies;

    /**
     * ful-film the collection with data from files into two collections: plain file and with calculated stats per each currency
     * In production world this service should not keep the data in memory and should call a data store.
     */
    @PostConstruct
    public void setUp() {
        this.currencies = currencyCsvReaderService.readCurrencyValues();
        this.extendedCurrencies = currencies.stream()
                .map(this::mapToExtendedCryptoCurrency)
                .collect(Collectors.toList());
    }

    /**
     * Return currency with prices on the time frame. The prices are sorted by date.
     */
    public List<ExtendedCurrency> getExtendedCurrencies() {
        return extendedCurrencies;
    }

    public List<CryptoCurrency> getAllCurrencies() {
        return currencies;
    }

    private ExtendedCurrency mapToExtendedCryptoCurrency(CryptoCurrency currency) {
        return ExtendedCurrency.builder()
                .currency(currency.getCurrencyName())
                .max(currency.getValues().stream()
                        .map(CryptoCurrency.Rate::getPrice)
                        .max(Comparator.naturalOrder()).orElse(null))
                .min(currency.getValues().stream()
                        .map(CryptoCurrency.Rate::getPrice)
                        .min(Comparator.naturalOrder()).orElse(null))
                .newest(currency.getValues().first().getPrice())
                .oldest(currency.getValues().last().getPrice())
                .build();
    }
}
