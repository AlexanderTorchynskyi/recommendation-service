package com.xm.recommendation.model;

import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.SortedSet;

@Builder
@Data
public class CryptoCurrency {

    private String currencyName;
    private SortedSet<Rate> values; 

    @Getter
    @RequiredArgsConstructor
    public enum Headers {
        TIMESTAMP("timestamp"), SYMBOL("symbol"), PRICE("price");
        private final String header;
    }

    @Builder
    @Data
    public static class Rate {
        private LocalDateTime date;
        private BigDecimal price;
    }
}
