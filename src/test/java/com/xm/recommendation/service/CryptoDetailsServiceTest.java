package com.xm.recommendation.service;

import com.xm.recommendation.dao.CurrencyValuesDao;
import com.xm.recommendation.model.CryptoCurrency;
import com.xm.recommendation.model.ExtendedCurrency;
import com.xm.recommendation.model.NormalisedCurrency;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.SortedSet;
import java.util.TreeSet;

import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CryptoDetailsServiceTest {

    private static final BigDecimal BIGGER_BTC_TEST_PRICE = new BigDecimal(2);
    private static final LocalDate TEST_LOCAL_DATE = LocalDate.parse("2022-01-01");
    private static final LocalDateTime OLDER_TEST_RATE_DATE_TIME = LocalDateTime.parse("2022-01-01T10:10:10");
    private static final LocalDateTime NEWER_TEST_RATE_DATE_TIME = LocalDateTime.parse("2022-01-01T12:10:10");

    @Mock
    private CurrencyValuesDao dao;
    @InjectMocks
    private CryptoDetailsService cryptoDetailsService;

    @Test
    void givenExistedCurrency_whenFindByCurrency_thenDataIsReturned_andNoExceptionIsThrown() {
        // arrange 
        when(dao.getExtendedCurrencies())
                .thenReturn(List.of(ExtendedCurrency.builder()
                        .currency("BTC")
                        .oldest(BigDecimal.ONE)
                        .newest(BIGGER_BTC_TEST_PRICE)
                        .max(BIGGER_BTC_TEST_PRICE)
                        .min(BigDecimal.ONE)
                        .build())
                );
        // execute
        final ExtendedCurrency actualCurrency = cryptoDetailsService.getByCurrency("BTC");
        // assert
        Assertions.assertEquals(ExtendedCurrency.builder()
                .currency("BTC")
                .max(BIGGER_BTC_TEST_PRICE)
                .min(BigDecimal.ONE)
                .newest(BIGGER_BTC_TEST_PRICE)
                .oldest(BigDecimal.ONE)
                .build(), actualCurrency);
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = "INVALID_CURRENCY_NAME")
    void givenRandomInvalidCurrencyName_whenCurrencyNameInvalid_thenExceptionIsThrown(String testValue) {
        // arrange 
        when(dao.getExtendedCurrencies()).thenReturn(Collections.emptyList());
        //execute & arrange
        Assertions.assertThrows(NoSuchElementException.class,
                () -> cryptoDetailsService.getByCurrency(testValue));
    }

    @Test
    void givenListOfRates_whenGetAllIsCalled_thenNormalisedCurrenciesAreCalculated() {
        // arrange 
        when(dao.getAllCurrencies())
                .thenReturn(Collections.singletonList(CryptoCurrency.builder()
                        .currencyName("BTC")
                        .values(aSortedRatesSet(BigDecimal.ONE, BIGGER_BTC_TEST_PRICE))
                        .build()));
        // execute
        final List<NormalisedCurrency> actualNormalisedCurrencies = cryptoDetailsService.getAll();
        // arrange
        Assertions.assertEquals(Collections.singletonList(NormalisedCurrency.builder()
                .currency("BTC").normalisedRange(new BigDecimal("1.000000")).build()), actualNormalisedCurrencies);
    }

    @Test
    void givenEmptySource_whenGetAllIsCalled_thenEmptyListIsReturned() {
        // arrange 
        when(dao.getAllCurrencies())
                .thenReturn(Collections.emptyList());
        // execute
        final List<NormalisedCurrency> actualNormalisedCurrencies = cryptoDetailsService.getAll();
        // arrange
        Assertions.assertEquals(Collections.emptyList(), actualNormalisedCurrencies);
    }

    @Test
    void givenTwoCurrencyWithRatesOnTheSameDate_whenFindTheHighestNormalisedInSpecifDate_thenCalculateAndReturnValue() {
        // arrange 
        when(dao.getAllCurrencies())
                .thenReturn(List.of(CryptoCurrency.builder()
                                .currencyName("BTC")
                                .values(aSortedRatesSet(BigDecimal.ONE, BIGGER_BTC_TEST_PRICE))
                                .build(),
                        CryptoCurrency.builder()
                                .currencyName("ETC")
                                .values(aSortedRatesSet(BigDecimal.ONE, BigDecimal.TEN))
                                .build()));
        // execute
        final NormalisedCurrency actualNormalisedCurrency =
                cryptoDetailsService.getCurrencyWithTheHighestNormalisedRangeByDate(TEST_LOCAL_DATE);
        // assert
        Assertions.assertEquals(NormalisedCurrency.builder()
                        .currency("ETC")
                        .normalisedRange(new BigDecimal("9.000000"))
                        .build(),
                actualNormalisedCurrency);
    }

    @Test
    void givenDateThatHasNoCurrencyRates_whenFindTheHighestNormalisedInSpecifDate_thenExceptionIsThrown() {
        // arrange 
        when(dao.getAllCurrencies())
                .thenReturn(Collections.singletonList(CryptoCurrency.builder()
                        .currencyName("ETC")
                        .values(aSortedRatesSet(BigDecimal.ONE, BigDecimal.TEN))
                        .build()));
        // execute & assert
        Assertions.assertThrows(NoSuchElementException.class,
                () -> cryptoDetailsService.getCurrencyWithTheHighestNormalisedRangeByDate(LocalDate.parse("1970-01-01")));
    }

    private static SortedSet<CryptoCurrency.Rate> aSortedRatesSet(BigDecimal priceTo1stRate, BigDecimal priceTo2ndtRate) {
        SortedSet<CryptoCurrency.Rate> rates = new TreeSet<>(Comparator.comparing(CryptoCurrency.Rate::getDate));
        rates.add(CryptoCurrency.Rate.builder()
                .date(OLDER_TEST_RATE_DATE_TIME)
                .price(priceTo1stRate).build());
        rates.add(CryptoCurrency.Rate.builder()
                .date(NEWER_TEST_RATE_DATE_TIME)
                .price(priceTo2ndtRate).build());
        return rates;
    }
}