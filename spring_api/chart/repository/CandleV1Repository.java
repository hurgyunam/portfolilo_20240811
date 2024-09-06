package com.lab2ai.repository.mongoRepository;

import com.lab2ai.data.entity.Candle;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.List;

@Repository
public class CandleV1Repository {
    @Qualifier("upbitCandle1mTemplateV1")
    @Autowired
    MongoTemplate mongoTemplate;
    @Qualifier("upbitCandle5mTemplateV1")
    @Autowired
    MongoTemplate mongoTemplate5;
    @Qualifier("upbitCandle15mTemplateV1")
    @Autowired
    MongoTemplate mongoTemplate15;
    @Qualifier("upbitCandle1HTemplateV1")
    @Autowired
    MongoTemplate mongoTemplate60;
    @Qualifier("upbitCandle4HTemplateV1")
    @Autowired
    MongoTemplate mongoTemplate240;
    @Qualifier("upbitCandleDayTemplateV1")
    @Autowired
    MongoTemplate mongoTemplateDay;
    @Qualifier("upbitCandleWeekTemplateV1")
    @Autowired
    MongoTemplate mongoTemplateWeek;
    @Qualifier("upbitCandleMonthTemplateV1")
    @Autowired
    MongoTemplate mongoTemplateMonth;

    private static final String timeKey = "candleDateTimeUtc";


    public BigDecimal getRecentCandlePrice(Long cryptoId) {
        Query query = new Query();
        query.with(Sort.by(Sort.Direction.DESC, "candleDateTimeUtc"));
        query.limit(1);

        Candle recentCandle = mongoTemplate.findOne(query, Candle.class, cryptoId.toString());

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

        Candle candle = mongoTemplate.findOne(query, Candle.class, cryptoId.toString());

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
                candleTemplate = mongoTemplate;
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
