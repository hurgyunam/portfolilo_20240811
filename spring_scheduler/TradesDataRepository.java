package com.lab2ai.cosign_earning_scheduler.spot.repository;

import com.lab2ai.cosign_earning_scheduler.spot.dto.TradeCoin;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import org.bson.Document;
import org.bson.types.Decimal128;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;


@Repository
public class TradesDataRepository {
    @Autowired
    @Qualifier("upbit_trade_client")
    private MongoClient client;

    @Autowired
    @Qualifier("upbit_trade_template")
    MongoTemplate mongoTemplate;

    final String table = "trade_upbit";

    /*
     * SignData()
     */

    public TradeCoin findBuyTrade(Long cryptoCurrencyId, LocalDateTime startTimeUtc, LocalDateTime buyTimeUtc, BigDecimal startPrice) {
        MongoCollection<Document> collection = client.getDatabase(table).getCollection(cryptoCurrencyId.toString());
        TradeCoin tradeData = null;

        Document findDocument = new Document();
        findDocument.put("main_trade_time_utc", new Document("$gte", startTimeUtc.toString()).append("$lt", buyTimeUtc.toString()));
        findDocument.put("trade_price", new Document("$lte", new Decimal128(startPrice)));

        Document sortDocument = new Document();
        sortDocument.put("main_trade_time_utc", 1);

        MongoCursor<Document> cursor = collection.find(findDocument).sort(sortDocument).limit(1).cursor();
        while (cursor.hasNext()) {
            tradeData = mongoTemplate.getConverter().read(TradeCoin.class, cursor.next());
        }

        return tradeData;
    }

    public TradeCoin findCutTrade(Long cryptoCurrencyId, LocalDateTime startTimeUtc, LocalDateTime endTimeUtc, BigDecimal cutPrice) {
        MongoCollection<Document> collection = client.getDatabase(table).getCollection(cryptoCurrencyId.toString());
        TradeCoin tradeData = null;

        Document findDocument = new Document();
        findDocument.put("main_trade_time_utc", new Document("$gte", startTimeUtc.toString()).append("$lt", endTimeUtc.toString()));
        findDocument.put("trade_price", new Document("$lte", new Decimal128(cutPrice)));

        Document sortDocument = new Document();
        sortDocument.put("main_trade_time_utc", 1);

        MongoCursor<Document> cursor = collection.find(findDocument).sort(sortDocument).limit(1).cursor();

        while (cursor.hasNext()) {
            tradeData = mongoTemplate.getConverter().read(TradeCoin.class, cursor.next());
        }

        return tradeData;
    }

    public TradeCoin findSellTrade(Long cryptoCurrencyId, String buyTimeUtc, LocalDateTime endTimeUtc, BigDecimal targetPrice) {
        MongoCollection<Document> collection = client.getDatabase(table).getCollection(cryptoCurrencyId.toString());
        TradeCoin tradeData = null;

        Document findDocument = new Document();
        findDocument.put("main_trade_time_utc", new Document("$gte", buyTimeUtc).append("$lte", endTimeUtc.toString()));
        findDocument.put("trade_price", new Document("$gte", new Decimal128(targetPrice)));

        Document sortDocument = new Document();
        sortDocument.put("main_trade_time_utc", 1);

        MongoCursor<Document> cursor = collection.find(findDocument).sort(sortDocument).limit(1).cursor();

        while (cursor.hasNext()) {
            tradeData = mongoTemplate.getConverter().read(TradeCoin.class, cursor.next());
        }

        return tradeData;
    }

    public TradeCoin findEndTrade(Long cryptoCurrencyId, LocalDateTime startTimeUtc, LocalDateTime endTimeUtc) {
        MongoCollection<Document> collection = client.getDatabase(table).getCollection(cryptoCurrencyId.toString());
        TradeCoin tradeData = null;

        Document timeCond = new Document("$gte", startTimeUtc.toString());
        timeCond.put("$lt", endTimeUtc.toString());

        Document findDocument = new Document("main_trade_time_utc", timeCond);

        Document sortDocument = new Document();
        sortDocument.put("main_trade_time_utc", -1);

        MongoCursor<Document> cursor = collection.find(findDocument).sort(sortDocument).limit(1).cursor();

        while (cursor.hasNext()) {
            tradeData = mongoTemplate.getConverter().read(TradeCoin.class, cursor.next());
        }

        return tradeData;
    }

    public TradeCoin findMinTrade(Long cryptoCurrencyId, LocalDateTime startTimeUtc, LocalDateTime endTimeUtc) {
        MongoCollection<Document> collection = client.getDatabase(table).getCollection(cryptoCurrencyId.toString());
        TradeCoin tradeData = null;

        Document findDocument = new Document();
        findDocument.put("main_trade_time_utc", new Document("$gte", startTimeUtc.toString()).append("$lte", endTimeUtc.toString()));

        Document sortDocument = new Document();
        sortDocument.put("trade_price", 1);

        MongoCursor<Document> cursor = collection.find(findDocument).sort(sortDocument).limit(1).cursor();

        while (cursor.hasNext()) {
            tradeData = mongoTemplate.getConverter().read(TradeCoin.class, cursor.next());
        }

        return tradeData;
    }

    public TradeCoin findMaxTrade(Long cryptoCurrencyId, LocalDateTime startTimeUtc, LocalDateTime endTimeUtc) {
        MongoCollection<Document> collection = client.getDatabase(table).getCollection(cryptoCurrencyId.toString());
        TradeCoin tradeData = null;

        Document findDocument = new Document();
        findDocument.put("main_trade_time_utc", new Document("$gte", startTimeUtc.toString()).append("$lte", endTimeUtc.toString()));

        Document sortDocument = new Document();
        sortDocument.put("trade_price", -1);

        MongoCursor<Document> cursor = collection.find(findDocument).sort(sortDocument).limit(1).cursor();

        while (cursor.hasNext()) {
            tradeData = mongoTemplate.getConverter().read(TradeCoin.class, cursor.next());
        }

        return tradeData;
    }

    public TradeCoin findEndNextTrade(Long cryptoCurrencyId, LocalDateTime endTimeUtc) {
        String endTimeStr = endTimeUtc.toString();

        MongoCollection<Document> collection = client.getDatabase(table).getCollection(cryptoCurrencyId.toString());
        TradeCoin tradeData = null;

        Document findDocument = new Document();
        findDocument.put("main_trade_time_utc", new Document("$gte", endTimeStr));

        Document sortDocument = new Document();
        sortDocument.put("main_trade_time_utc", 1);

        MongoCursor<Document> cursor = collection.find(findDocument).sort(sortDocument).limit(1).cursor();

        while (cursor.hasNext()) {
            tradeData = mongoTemplate.getConverter().read(TradeCoin.class, cursor.next());
        }

        return tradeData;
    }


}
