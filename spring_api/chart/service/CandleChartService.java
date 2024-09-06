package com.lab2ai.service;

import com.lab2ai.data.dto.*;
import com.lab2ai.data.entity.Candle;
import com.lab2ai.repository.mongoRepository.CandleFuturesRepository;
import com.lab2ai.repository.mongoRepository.CandleV1Repository;
import com.lab2ai.repository.queryDsl.CryptoAssetQRepository;
import com.lab2ai.repository.queryDsl.CryptoCurrencyQRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class CandleChartService {
    private final DateTimeFormatter _formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:SS");

    @Autowired
    private CryptoAssetQRepository cryptoAssetQRepository;
    @Autowired
    private CandleV1Repository candleV1Repository;
    @Autowired
    private CryptoCurrencyService cryptoCurrencyService;
    @Autowired
    private CandleFuturesRepository candleFuturesRepository;
    @Autowired
    private CryptoCurrencyQRepository cryptoCurrencyQRepository;

    private final List<String> resolutions = Arrays.asList("1", "5", "15", "1H", "4H", "1D", "7D", "30D");

    public ConfigRes config() {
        return ConfigRes.builder()
                .supported_resoltion(resolutions)
                .supports_group_request(false)
                .supports_marks(false)
                .supports_search(true)
                .supports_timescale_marks(false)
                .build();
    }

    @Deprecated
    public SymbolResV1 getV1Symbols(SymbolReq request) {
        CryptoAsset cryptoAsset = cryptoAssetQRepository.findById(Long.parseLong(request.getSymbol()));
        BigDecimal tradePrice = candleV1Repository.getRecentCandlePrice(Long.parseLong(request.getSymbol()));

        int priceScale = getPriceScale(tradePrice);

        return new SymbolResV1(Long.parseLong(request.getSymbol()), cryptoAsset.getKoreanName(), priceScale, resolutions);
    }

    public SymbolResV2 getV2Symbols(SymbolReq request) {

        BigDecimal tradePrice;
        CryptoAsset cryptoAsset;
        String exchange = "";
        List<CryptoAsset> cryptoAssetList = cryptoCurrencyService.getSameDigitalAssetsIdList(Long.parseLong(request.getSymbol()));

        if(cryptoAssetList.size()>=2){

            cryptoAsset = cryptoAssetList.stream()
                    .filter(cryptoAssetItem -> cryptoAssetItem.getMarket().contains("KRW-".toUpperCase()))
                    .collect(Collectors.toList()).get(0);

            Long upbitCryptoId = cryptoCurrencyQRepository.findByCryptoFullCode(cryptoAsset.getMarket()).get().getCryptoId();

            tradePrice = candleV1Repository.getRecentCandlePrice(upbitCryptoId);
            exchange = "upbit";
        } else{

            cryptoAsset = cryptoAssetList.get(0);

            if(cryptoAsset.getMarket().contains("KRW-".toUpperCase())){
                tradePrice = candleV1Repository.getRecentCandlePrice(Long.parseLong(request.getSymbol()));
                exchange = "upbit";
            } else{
                tradePrice = candleFuturesRepository.getRecentCandlePrice(Long.parseLong(request.getSymbol()));
                exchange = "binance";
            }

        }
        int priceScale = getPriceScale(tradePrice);
        return new SymbolResV2(Long.parseLong(request.getSymbol()), cryptoAsset.getKoreanName(), priceScale, resolutions,exchange,cryptoAsset.getCryptoFullCode());
    }


    @Deprecated
    public HistoryRes getV1History(HistoryReq req) {
        Long cryptoId = Long.parseLong(req.getSymbol());
        LocalDateTime endTime = LocalDateTime.ofEpochSecond(req.getTo(), 0, ZoneOffset.UTC);
        LocalDateTime startTime = null;

        if (req.getCountback() != null) {
            Integer realMinutes = getRealMinutesByResolutionString(req.getResolution());
            startTime = endTime.minusMinutes(realMinutes * req.getCountback());
        } else {
            startTime = LocalDateTime.ofEpochSecond(req.getFrom(), 0, ZoneOffset.UTC);
        }

        String startTimeText = startTime.format(_formatter);
        String endTimeText = endTime.format(_formatter);
        List<Candle> candles = candleV1Repository.getCandles(cryptoId, startTimeText, endTimeText, req.getResolution());

        if (candles.size() > 0) {
            return new HistoryRes(candles);
        } else {
            Long nextTime = candleV1Repository.getNextTime(cryptoId, startTimeText);

            return new HistoryRes(candles, nextTime);
        }
    }

    public HistoryRes getV2History(HistoryReq req) {

        Long cryptoId = Long.parseLong(req.getSymbol());
        LocalDateTime endTime = LocalDateTime.ofEpochSecond(req.getTo(), 0, ZoneOffset.UTC);
        LocalDateTime startTime = null;

        if (req.getCountback() != null) {
            Integer realMinutes = getRealMinutesByResolutionString(req.getResolution());
            startTime = endTime.minusMinutes(realMinutes * req.getCountback());
        } else {
            startTime = LocalDateTime.ofEpochSecond(req.getFrom(), 0, ZoneOffset.UTC);
        }

        String startTimeText = startTime.format(_formatter);
        String endTimeText = endTime.format(_formatter);

        // 캔들 뿌려주기
        List<Candle> candles = getCandleByExchange(cryptoId, startTimeText, endTimeText, req);

        if (candles.size() > 0) {
            return new HistoryRes(candles);
        } else {
            Long nextTime = candleV1Repository.getNextTime(cryptoId, startTimeText);
            return new HistoryRes(candles, nextTime);
        }
    }

    private List<Candle> getCandleByExchange(Long cryptoId, String startTimeText, String endTimeText, HistoryReq req) {

        CryptoAsset cryptoAsset;
        List<CryptoAsset> cryptoAssetList = cryptoCurrencyService.getSameDigitalAssetsIdList(cryptoId);

        if(cryptoAssetList.size()>=2){

            cryptoAsset = cryptoAssetList.stream()
                    .filter(cryptoAssetItem -> cryptoAssetItem.getMarket().contains("KRW-".toUpperCase()))
                    .collect(Collectors.toList()).get(0);

            cryptoId = cryptoCurrencyQRepository.findByCryptoFullCode(cryptoAsset.getMarket()).get().getCryptoId();
            return candleV1Repository.getCandles(cryptoId, startTimeText, endTimeText, req.getResolution());
        } else{

            cryptoAsset = cryptoAssetList.get(0);

            if(cryptoAsset.getMarket().contains("KRW-".toUpperCase())){
                return candleV1Repository.getCandles(cryptoId, startTimeText, endTimeText, req.getResolution());
            } else{
                return candleFuturesRepository.getCandles(cryptoId, startTimeText, endTimeText, req.getResolution());
            }
        }
    }

    /**
     * @param requestPrice
     * @return 호가 단위에 문제가 없는 경우 true
     */
    private int getPriceScale(BigDecimal requestPrice) {
        if (requestPrice.compareTo(new BigDecimal(100d)) >= 0 && requestPrice.compareTo(new BigDecimal(2000000d)) < 0) {
            return 1;
        } else if (requestPrice.compareTo(new BigDecimal(10d)) >= 0 && requestPrice.compareTo(new BigDecimal(100d)) < 0) {
            return 10;
        } else if (requestPrice.compareTo(new BigDecimal(1d)) >= 0 && requestPrice.compareTo(new BigDecimal(10d)) < 0) {
            return 100;
        } else if (requestPrice.compareTo(new BigDecimal(0.1d)) >= 0 && requestPrice.compareTo(new BigDecimal(1d)) < 0) {
            return 1000;
        } else if (requestPrice.compareTo(new BigDecimal(0.1d)) < 0) {
            return 10000;
        } else {
            System.out.println("호가 표시 단위에 어긋납니다.");
            return 1;
        }
    }

    public Integer getRealMinutesByResolutionString(String resolution) {
        switch (resolution) {
            case "1":
                return 1;
            case "5":
                return 5;
            case "15":
                return 15;
            case "60":
            case "1H":
                return 60;
            case "240":
            case "4H":
                return 4 * 60;
            case "1D":
                return 24 * 60;
            case "7D":
                return 7 * 24 * 60;
            case "30D":
                return 30 * 24 * 60;
            default:
                return 0;
        }
    }
}
