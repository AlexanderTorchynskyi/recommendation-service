package com.xm.recommendation.api;

import com.xm.recommendation.model.ExtendedCurrency;
import com.xm.recommendation.model.NormalisedCurrency;
import com.xm.recommendation.service.CryptoDetailsService;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.List;

@Slf4j
@RestController
@RequestMapping(path = "/api/currency")
@RequiredArgsConstructor
public class CryptoCurrencyRestController {

    private final CryptoDetailsService cryptoDetailsService;

    @ApiOperation(value = "Return min, max, new and old prices for specific crypto currency")
    @ApiResponses({
            @ApiResponse(code = 200, message = "Crypto currency found and response is successful"),
            @ApiResponse(code = 400, message = "No such currency")
    })
    @GetMapping("/{currency}")
    public ResponseEntity<ExtendedCurrency> getByCurrency(@PathVariable(value = "currency") String currency) {
        return ResponseEntity.ok(cryptoDetailsService.getByCurrency(currency));
    }

    @ApiOperation(value = "Return list of currencies along with normalised price range, based on provided file.")
    @ApiResponses({
            @ApiResponse(code = 200, message = "Data exist, and successfully returned")
    })
    @GetMapping("/normalise")
    public ResponseEntity<List<NormalisedCurrency>> getAllNormalised() {
        return ResponseEntity.ok(cryptoDetailsService.getAll());
    }

    @ApiOperation(value = "Return a currency that has the highest normalised price range in specific day")
    @ApiResponses({
            @ApiResponse(code = 200, message = "Data exist, and successfully returned"),
            @ApiResponse(code = 400, message = "No data to return per provided day")
    })
    @GetMapping("/normalise-max")
    public ResponseEntity<NormalisedCurrency> getMaxNormalised(
            @ApiParam(name = "day", type = "String",
                    value = "a date to get the highest normalised price currency. Date format: yyy-mm-dd",
                    example = "2022-01-01", required = true)
            @RequestParam(value = "day") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate day) {
        return ResponseEntity.ok(cryptoDetailsService.getCurrencyWithTheHighestNormalisedRangeByDate(day));
    }
}
