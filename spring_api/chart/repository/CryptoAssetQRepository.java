package com.lab2ai.repository.queryDsl;

import com.lab2ai.data.dto.CryptoAsset;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;

import static com.lab2ai.data.entity.QCryptoCurrency.cryptoCurrency;
import static com.lab2ai.data.entity.QDigitalAssets.digitalAssets;


@Repository
public class CryptoAssetQRepository {
    @Autowired
    private JPAQueryFactory query;
    public CryptoAsset findById(Long cryptoId) {
        return query.select(Projections.constructor(CryptoAsset.class, cryptoCurrency, digitalAssets))
                .from(cryptoCurrency)
                .innerJoin(digitalAssets)
                .on(cryptoCurrency.digitalAssetsId.eq(digitalAssets.digitalAssetsId))
                .where(cryptoCurrency.cryptoId.eq(cryptoId))
                .fetchFirst();
    }

    public String findCryptoFullCodeById(Long cryptoId) {
        return query.select(cryptoCurrency)
                .from(cryptoCurrency)
                .where(cryptoCurrency.cryptoId.eq(cryptoId))
                .fetchFirst()
                .getCryptoFullCode();
    }

    public List<CryptoAsset> findByDigitalAssetsId(Long digitalAssetsId) {
        return query.select(Projections.constructor(CryptoAsset.class, cryptoCurrency, digitalAssets))
                .from(cryptoCurrency)
                .innerJoin(digitalAssets)
                .on(cryptoCurrency.digitalAssetsId.eq(digitalAssets.digitalAssetsId))
                .where(cryptoCurrency.digitalAssetsId.eq(digitalAssetsId))
                .fetch();
    }
}
