package com.xm.recommendation.service;

import com.xm.recommendation.dao.CurrencyValuesDao;
import com.xm.recommendation.model.CryptoCurrency;
import com.xm.recommendation.model.ExtendedCurrency;
import com.xm.recommendation.model.NormalisedCurrency;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CryptoDetailsService {

    private static final int FRACTION_NUMBER = 6;
    private final CurrencyValuesDao currencyValuesDao;

    public ExtendedCurrency getByCurrency(String currency) {
        return currencyValuesDao.getExtendedCurrencies().stream()
                .filter(cryptoCurrency -> cryptoCurrency.getCurrency().equalsIgnoreCase(currency))
                .findFirst()
                .orElseThrow(() -> new NoSuchElementException(String.format("No such currency found: '%s'", currency)));
    }

    public List<NormalisedCurrency> getAll() {
        return currencyValuesDao.getAllCurrencies().stream()
                .map(cryptoCurrency -> this.calculateNormalisedPrice(cryptoCurrency.getValues(), cryptoCurrency.getCurrencyName()))
                .map(normalisedPrice -> NormalisedCurrency.builder()
                        .currency(normalisedPrice.getLeft())
                        .normalisedRange(normalisedPrice.getRight())
                        .build())
                .sorted(Comparator.comparing(NormalisedCurrency::getNormalisedRange).reversed())
                .collect(Collectors.toList());
    }

    public NormalisedCurrency getCurrencyWithTheHighestNormalisedRangeByDate(LocalDate date) {
        return currencyValuesDao.getAllCurrencies().stream()
                .map(cryptoCurrency -> Pair.of(cryptoCurrency.getCurrencyName(), cryptoCurrency.getValues().stream()
                        .filter(rate -> rate.getDate().toLocalDate().equals(date))
                        .collect(Collectors.toList())))
                .filter(pair -> !pair.getRight().isEmpty())
                .map(rate -> calculateNormalisedPrice(rate.getRight(), rate.getLeft()))
                .max(Comparator.comparing(Pair::getRight))
                .map(normalisedPrice -> NormalisedCurrency.builder()
                        .currency(normalisedPrice.getLeft())
                        .normalisedRange(normalisedPrice.getRight())
                        .build())
                .orElseThrow(() ->
                        new NoSuchElementException(String.format("could not calculate max normalised range for the date: '%s'", date)));
    }

    private Pair<String, BigDecimal> calculateNormalisedPrice(Collection<CryptoCurrency.Rate> currencyRate, String name) {
        final BigDecimal maxPrice = currencyRate.stream().map(CryptoCurrency.Rate::getPrice).max(Comparator.naturalOrder())
                .orElse(BigDecimal.ZERO);
        final BigDecimal minPrice = currencyRate.stream().map(CryptoCurrency.Rate::getPrice).min(Comparator.naturalOrder())
                .orElse(BigDecimal.ZERO);
        final BigDecimal normalisedPrice = (maxPrice.subtract(minPrice)).divide(minPrice, FRACTION_NUMBER, RoundingMode.HALF_UP);
        return Pair.of(name, normalisedPrice);
    }
}
