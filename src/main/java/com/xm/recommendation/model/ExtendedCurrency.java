package com.xm.recommendation.model;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Builder
@Data
public class ExtendedCurrency {
    private String currency;
    private BigDecimal newest;
    private BigDecimal oldest;
    private BigDecimal min;
    private BigDecimal max;
}
