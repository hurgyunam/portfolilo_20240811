package com.lab2ai.controller.v1;

import com.lab2ai.aspect.LatestVersion;
import com.lab2ai.data.dto.*;
import com.lab2ai.service.CandleChartService;
import io.swagger.v3.oas.annotations.Hidden;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.sql.Timestamp;
import java.time.LocalDateTime;

@Hidden
@RestController
@RequestMapping("/api/coin/tv_chart")
public class CoinChartController {
    @Autowired
    private CandleChartService candleChartService;

    @LatestVersion
    @GetMapping("/v1/config")
    public ConfigRes v1config() {
        return candleChartService.config();
    }

    @LatestVersion
    @GetMapping("/v1/time")
    public long v1time() {
        return Timestamp.valueOf(LocalDateTime.now()).getTime();
    }

    @Deprecated
    @GetMapping("/v1/symbols")
    public SymbolResV1 v1symbols(SymbolReq request) {
        return candleChartService.getV1Symbols(request);
    }

    @Deprecated
    @GetMapping("/v1/history")
    public HistoryRes v1history(HistoryReq req) {
        return candleChartService.getV1History(req);
    }
}
