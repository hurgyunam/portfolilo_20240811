package com.lab2ai.repository.mongoRepository;

import com.lab2ai.data.entity.Candle;
import com.mongodb.client.MongoClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.stream.Collectors;

@Repository
public class CandleFuturesRepository {

    @Qualifier("binance_candle_1")
    @Autowired
    MongoClient client1;
    @Qualifier("binance_candle_1_template")
    @Autowired
    MongoTemplate mongoTemplate1;

    @Qualifier("binance_candle_5")
    @Autowired
    MongoClient client5;
    @Qualifier("binance_candle_5_template")
    @Autowired
    MongoTemplate mongoTemplate5;

    @Qualifier("binance_candle_15")
    @Autowired
    MongoClient client15;
    @Qualifier("binance_candle_15_template")
    @Autowired
    MongoTemplate mongoTemplate15;

    @Qualifier("binance_candle_60")
    @Autowired
    MongoClient client60;
    @Qualifier("binance_candle_60_template")
    @Autowired
    MongoTemplate mongoTemplate60;

    @Qualifier("binance_candle_240_template")
    @Autowired
    MongoTemplate mongoTemplate240;

    @Qualifier("binance_candle_day_template")
    @Autowired
    MongoTemplate mongoTemplateDay;

    @Qualifier("binance_candle_week_template")
    @Autowired
    MongoTemplate mongoTemplateWeek;

    @Qualifier("binance_candle_month_template")
    @Autowired
    MongoTemplate mongoTemplateMonth;

    private static final String timeKey = "candleDateTimeUtc";


    public List<Candle> getCandles(Long cryptoId, String gteTime, String lteTime) {

        Query query = new Query();
        query.addCriteria(Criteria.where("candleDateTimeUtc").gte(gteTime).lte(lteTime));
        query.with(Sort.by(Sort.Direction.ASC, "candleDateTimeUtc"));
        query.with(Sort.by(Sort.Direction.ASC, "_id"));
        return mongoTemplate1.find(query, Candle.class, cryptoId.toString());
    }

    public List<Candle> get5Candles(Long cryptoId, String gteTime, String lteTime) {

        Query query = new Query();
        query.addCriteria(Criteria.where("candleDateTimeUtc").gte(gteTime).lte(lteTime));
        query.with(Sort.by(Sort.Direction.ASC, "candleDateTimeUtc"));
        query.with(Sort.by(Sort.Direction.ASC, "_id"));
        return mongoTemplate5.find(query, Candle.class, cryptoId.toString());
    }

    public List<Candle> get15Candles(Long cryptoId, String gteTime, String lteTime) {

        Query query = new Query();
        query.addCriteria(Criteria.where("candleDateTimeUtc").gte(gteTime).lte(lteTime));
        query.with(Sort.by(Sort.Direction.ASC, "candleDateTimeUtc"));
        query.with(Sort.by(Sort.Direction.ASC, "_id"));
        return mongoTemplate15.find(query, Candle.class, cryptoId.toString());
    }
    public List<Candle> get60Candles(Long cryptoId, String gteTime, String lteTime) {

        Query query = new Query();
        query.addCriteria(Criteria.where("candleDateTimeUtc").gte(gteTime).lte(lteTime));
        query.with(Sort.by(Sort.Direction.ASC, "candleDateTimeUtc"));
        query.with(Sort.by(Sort.Direction.ASC, "_id"));
        return mongoTemplate60.find(query, Candle.class, cryptoId.toString());
    }

    public BigDecimal getRecentCandlePrice(Long cryptoId) {
        Query query = new Query();
        query.with(Sort.by(Sort.Direction.DESC, "candleDateTimeUtc"));
        query.limit(1);

        Candle recentCandle = mongoTemplate1.findOne(query, Candle.class, cryptoId.toString());

        return recentCandle.getTradePrice();
    }

    public List<Candle> getCandles(Long cryptoId, String gteTime, String lteTime, String resolution) {
        MongoTemplate candleTemplate = _getMongoTemplate(resolution);

        Query query = new Query();
        query.addCriteria(Criteria.where(timeKey).gte(gteTime).lte(lteTime));
        query.with(Sort.by(Sort.Direction.ASC, timeKey));
        List<Candle> candles = candleTemplate.find(query, Candle.class, cryptoId.toString());

        return candles;
    }

    public Long getNextTime(Long cryptoId, String lteTime) {
        Query query = new Query();
        query.addCriteria(Criteria.where(timeKey).lte(lteTime));
        query.with(Sort.by(Sort.Direction.DESC, timeKey));
        query.limit(1);

        Candle candle = mongoTemplate1.findOne(query, Candle.class, cryptoId.toString());

        if(candle != null) {
            return Timestamp.valueOf(candle.getCandleDateTimeUtc()).getTime() / 1000;
        } else {
            return null;
        }
    }

    private MongoTemplate _getMongoTemplate(String resolution) {
        MongoTemplate candleTemplate;

        switch (resolution) {
            case "1":
                candleTemplate = mongoTemplate1;
                break;
            case "5":
                candleTemplate = mongoTemplate5;
                break;
            case "15":
                candleTemplate = mongoTemplate15;
                break;
            case "60":
            case "1H":
                candleTemplate = mongoTemplate60;
                break;
            case "240":
            case "4H":
                candleTemplate = mongoTemplate240;
                break;
            case "1D":
                candleTemplate = mongoTemplateDay;
                break;
            case "7D":
                candleTemplate = mongoTemplateWeek;
                break;
            case "30D":
                candleTemplate = mongoTemplateMonth;
                break;
            default:
                throw new IllegalArgumentException("올바르지 않는 resolution 값입니다. " + resolution);
        }

        return candleTemplate;
    }

}
