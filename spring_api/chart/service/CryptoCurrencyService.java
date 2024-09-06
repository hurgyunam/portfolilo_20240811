package com.lab2ai.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.lab2ai.data.dto.*;
import com.lab2ai.data.enums.CryptoState;
import com.lab2ai.repository.queryDsl.CryptoAssetQRepository;
import com.lab2ai.repository.queryDsl.CryptoCurrencyQRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.CacheManager;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * 2022-04-18 오후 4:10
 * 코인 서비스
 *
 * @author Jay
 * @version 1.0
 */

@Service
public class CryptoCurrencyService {
    @Autowired
    private CacheManager cacheManager;
    @Autowired
    private CryptoCurrencyQRepository cryptoCurrencyQRepository;
    @Autowired
    private CryptoAssetQRepository cryptoAssetQRepository;
    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @Transactional(readOnly = true)
    public List<CryptoCurrencyItem> getAllCryptoCacheV3() throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        ValueOperations<String, Object> cryptoCacheOperations = redisTemplate.opsForValue();

        String upbitCryptosJson = String.valueOf(cryptoCacheOperations.get("cryptoCurrency:upbit:all"));
        List<CryptoCurrencyItem> upbitCryptoList = objectMapper.readValue(upbitCryptosJson, new TypeReference<List<CryptoCurrencyItem>>() {});

        String binanceCryptosJson = String.valueOf(cryptoCacheOperations.get("crypto:binance:futures:all"));
        List<CryptoCurrencyItem> binanceCryptoList = getBinanceCryptoCurrencyItems(objectMapper, binanceCryptosJson);

        return getUpbitAndBinanceCryptoCurrencyItems(upbitCryptoList, binanceCryptoList);
    }

    /**
     * 업비트-바이낸스 공통 코인 바이낸스쪽에 삭제
     * 업비트, 바이낸스에만 있는 코인들을 반환
     */
    private List<CryptoCurrencyItem> getUpbitAndBinanceCryptoCurrencyItems(List<CryptoCurrencyItem> upbitCryptoList, List<CryptoCurrencyItem> binanceCryptoList) {

        List<CryptoCurrencyItem> cryptoCurrencyItems = new ArrayList<>();
        for(CryptoCurrencyItem upbitCrypto : upbitCryptoList){
            for(CryptoCurrencyItem binanceCrypto : binanceCryptoList){
                if(upbitCrypto.getDigitalAssets().getDigitalAssetsId() == binanceCrypto.getDigitalAssets().getDigitalAssetsId()) {
                    binanceCryptoList.remove(binanceCrypto);
                    break;
                }
            }
        }

        cryptoCurrencyItems.addAll(upbitCryptoList);
        cryptoCurrencyItems.addAll(binanceCryptoList);

        return cryptoCurrencyItems;
    }

    private List<CryptoCurrencyItem> getBinanceCryptoCurrencyItems(ObjectMapper objectMapper, String binanceCryptosJson) throws JsonProcessingException {
        List<BinanceCryptoCurrencyItem> binanceCryptoCurrencyItemList = objectMapper.readValue(binanceCryptosJson, new TypeReference<List<BinanceCryptoCurrencyItem>>() {});

        List<Long> binanceCryptoIdList = binanceCryptoCurrencyItemList.stream()
                .map(binanceCryptoCurrencyItem -> binanceCryptoCurrencyItem.getCryptoId())
                .collect(Collectors.toList());

        List<CryptoCurrencyItem> binanceCryptoList = cryptoCurrencyQRepository.findByBinanceCryptoIdList(binanceCryptoIdList);
        return binanceCryptoList;
    }


    @Deprecated
    public List<CryptoCurrencyItem> getAllCryptoCacheV2() throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        ValueOperations<String, Object> cryptoCacheOperations = redisTemplate.opsForValue();
        List<CryptoCurrencyItem> cryptoCurrencyItems;

        if (Objects.isNull(cryptoCacheOperations.get("cryptoCurrency:upbit:all"))) {
            List<CryptoCurrencyItem> allCryptoCurrency = cryptoCurrencyQRepository.findAllCryptoCurrency();
            cryptoCurrencyItems = allCryptoCurrency.stream().filter(l -> l.getExchange().getExchangeName().equals("UPBIT"))
                    .filter(l -> l.getCryptoStatus().equals(CryptoState.ACTIVE))
                    .collect(Collectors.toList());
            cryptoCacheOperations.set("cryptoCurrency:upbit:all", objectMapper.writeValueAsString(cryptoCurrencyItems));
        }

        String cryptosJson = String.valueOf(cryptoCacheOperations.get("cryptoCurrency:upbit:all"));
        cryptoCurrencyItems = objectMapper.readValue(cryptosJson, new TypeReference<List<CryptoCurrencyItem>>() {
        });

        return cryptoCurrencyItems;
    }

    @Transactional(readOnly = true)
    public List<CryptoCodeItem> getCryptoCodeItem() throws JsonProcessingException {
        List<CryptoCurrencyItem> cryptoCurrencyItemList = getAllCryptoCacheV2();

        return cryptoCurrencyItemList.stream()
                .map(cryptoCurrencyItem ->
                        CryptoCodeItem.createdCryptoCurrencySmallItem(
                                cryptoCurrencyItem.getDigitalAssets().getDigitalAssetsCode(),cryptoCurrencyItem.getCryptoId()
                        )
                )
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public CryptoAssetResponse getSpotCryptoOrFuturesCrypto(Long cryptoId) {
        List<CryptoAsset> cryptoAssetList = getSameDigitalAssetsIdList(cryptoId);
        CryptoAssetResponse cryptoAssetResponse = setCryptoId(cryptoAssetList);
        cryptoAssetResponse.createCryptoAssetList(isSpotOrFuturesCryptoAssetValid(cryptoAssetList));
        return cryptoAssetResponse;
    }

    public List<CryptoAsset> getSameDigitalAssetsIdList(Long cryptoId) {
        CryptoCurrencyItem cryptoCurrencyItem = cryptoCurrencyQRepository.findByCryptoId(cryptoId).orElseThrow(() -> new IllegalArgumentException("없는 cryptoId 입니다."));
        Long digitalAssetsId = cryptoCurrencyItem.getDigitalAssets().getDigitalAssetsId();
        List<CryptoAsset> cryptoAssetList = cryptoAssetQRepository.findByDigitalAssetsId(digitalAssetsId);
        return cryptoAssetList;
    }

    private CryptoAssetResponse setCryptoId(List<CryptoAsset> cryptoAssetList) {
        CryptoAssetResponse cryptoAssetResponse = new CryptoAssetResponse();

        for(CryptoAsset cryptoAsset : cryptoAssetList){
            if(cryptoAsset.getMarket().contains("KRW-".toUpperCase())){
                cryptoAssetResponse.setUpbitCryptoId(cryptoAsset.getCryptoId());
            } else{
                cryptoAssetResponse.setBinanceCryptoId(cryptoAsset.getCryptoId());
            }
        }
        return cryptoAssetResponse;
    }

    private CryptoAsset isSpotOrFuturesCryptoAssetValid(List<CryptoAsset> cryptoAssetList) {
        CryptoAsset cryptoAsset;
        if(cryptoAssetList.size()>=2){
            cryptoAsset = cryptoAssetList.stream()
                    .filter(cryptoAssetItem -> cryptoAssetItem.getMarket().contains("KRW-".toUpperCase()))
                    .collect(Collectors.toList()).get(0);

            cryptoAsset.setSpotTradeType();

            return cryptoAsset;

        } else{

            cryptoAsset = cryptoAssetList.get(0);

            if(cryptoAsset.getMarket().contains("KRW-".toUpperCase())){
                cryptoAsset.setSpotTradeType();
            } else{
                cryptoAsset.setFuturesTradeType();
            }

            return cryptoAsset;
        }
    }
}
