package com.xm.recommendation.model;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Builder
@Data
public class NormalisedCurrency {
    private String currency;
    private BigDecimal normalisedRange;
}
