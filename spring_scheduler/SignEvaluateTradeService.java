package com.lab2ai.cosign_earning_scheduler.spot.service;

import com.lab2ai.cosign_earning_scheduler.ai_model.repository.SignRepository;
import com.lab2ai.cosign_earning_scheduler.core.dto.EvaluateResult;
import com.lab2ai.cosign_earning_scheduler.core.entity.Sign;
import com.lab2ai.cosign_earning_scheduler.core.enums.EvaluateState;
import com.lab2ai.cosign_earning_scheduler.core.enums.EvaluateState2;
import com.lab2ai.cosign_earning_scheduler.spot.dto.StateTradesGroup;
import com.lab2ai.cosign_earning_scheduler.spot.dto.TradeCoin;
import com.lab2ai.cosign_earning_scheduler.spot.enums.JudgeEndState;
import com.lab2ai.cosign_earning_scheduler.spot.enums.JudgeState;
import com.lab2ai.cosign_earning_scheduler.spot.repository.TradesDataRepository;
import com.lab2ai.cosign_earning_scheduler.spot.util.FeedRestTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class SignEvaluateTradeService {
    @Autowired
    private TradesDataRepository tradesDataRepository;
    @Autowired
    private FeedRestTemplate feedRestTemplate;
    @Autowired
    private SignRepository signRepository;

    public LocalDateTime getBuyLimitTime(LocalDateTime startTime, Integer validateMin) {
        return startTime.plusMinutes(validateMin / 2);
    }

    public StateTradesGroup buildStateTradesGroup(Sign sign) {
        LocalDateTime buyLimitTime = getBuyLimitTime(sign.getStartTime(), sign.getValidateMin());

        TradeCoin endNextTrade = tradesDataRepository.findEndNextTrade(sign.getCryptoCurrencyId(), sign.getEndTime());

        TradeCoin buyTrade = tradesDataRepository.findBuyTrade(sign.getCryptoCurrencyId(), sign.getStartTime(), buyLimitTime, sign.getStartPrice());

        LocalDateTime startOrBuyTime = buyTrade != null? buyTrade.getTradeTimeUtc() : sign.getStartTime();

        TradeCoin cutTrade = tradesDataRepository.findCutTrade(sign.getCryptoCurrencyId(), startOrBuyTime, sign.getEndTime(), sign.getCutPrice());

        TradeCoin sellTrade, sellTrade2, sellTrade3;

        if(buyTrade != null) {
            sellTrade = tradesDataRepository.findSellTrade(sign.getCryptoCurrencyId(), buyTrade.getTradeTimeUtc().toString(), sign.getEndTime(), sign.getTargetPrice());

            if(sign.getTargetPrice2() != null) {
                sellTrade2 = tradesDataRepository.findSellTrade(sign.getCryptoCurrencyId(), buyTrade.getTradeTimeUtc().toString(), sign.getEndTime(), sign.getTargetPrice2());
            } else {
                sellTrade2 = null;
            }

            if(sign.getTargetPrice3() != null) {
                sellTrade3 = tradesDataRepository.findSellTrade(sign.getCryptoCurrencyId(), buyTrade.getTradeTimeUtc().toString(), sign.getEndTime(), sign.getTargetPrice3());
            } else {
                sellTrade3 = null;
            }
        } else {
            sellTrade = sellTrade2 = sellTrade3 = null;
        }

        TradeCoin buyNextTrade = tradesDataRepository.findEndNextTrade(sign.getCryptoCurrencyId(), buyLimitTime);

        return StateTradesGroup.builder()
                .buyLimitTime(buyLimitTime)
                .startOrBuyTime(startOrBuyTime)
                .endNextTrade(endNextTrade)
                .buyTrade(buyTrade)
                .cutTrade(cutTrade)
                .sellTrade(sellTrade)
                .sellTrade2(sellTrade2)
                .sellTrade3(sellTrade3)
                .buyNextTrade(buyNextTrade)
                .build();
    }

    public EvaluateState2 judge(StateTradesGroup group) {
        JudgeEndState endState = judgeEnd(group.getBuyTrade(), group.getEndNextTrade(), group.getBuyNextTrade());

        if(endState == JudgeEndState.end) {
            JudgeState judgeState = judgeCutTime(group);

            switch (judgeState) {
                case target:
                    return EvaluateState2.sell_success;
                case target2:
                    return EvaluateState2.sell_success2;
                case target3:
                    return EvaluateState2.sell_success3;
                case cut:
                    return EvaluateState2.cut;
                default: // none
                    return group.getBuyTrade() != null? EvaluateState2.sell_fail: EvaluateState2.buy_fail;
            }
        } else if(endState == JudgeEndState.sell_wait) {
            JudgeState judgeState = judgeCutTime(group);

            switch (judgeState) {
                case target:
                    return EvaluateState2.sell_wait2; // 목표가 1을 넘었다
                case target2:
                    return EvaluateState2.sell_wait3; // 목표가 2를 넘었다
                case target3:
                    return EvaluateState2.sell_success3; // 목표가 3을 넘었다 => 조기 종료
                case cut:
                    return EvaluateState2.cut;
                default: // none
                    return EvaluateState2.sell_wait;
            }
        } else if(endState == JudgeEndState.buy_wait) {
            return EvaluateState2.buy_wait;
        } else { // endState == JudgeEndState.buy_fail
            return EvaluateState2.buy_fail;
        }
    }

    public Long getSlideNo(UUID mentorPageId) {
        Long slideNo = signRepository.getNextSlideNo(mentorPageId);

        if(slideNo == null) {
            slideNo = 1L;
        }

        return slideNo;
    }

    public EvaluateState getEvaluateStateBy2(EvaluateState2 state2) {
        switch (state2) {
            case sell_success:
            case sell_success2:
            case sell_success3:
                return EvaluateState.sell_success;
            case sell_wait:
            case sell_wait2:
            case sell_wait3:
                return EvaluateState.sell_wait;
            case buy_fail:
                return EvaluateState.buy_fail;
            case buy_wait:
                return EvaluateState.buy_wait;
            case sell_fail:
                return EvaluateState.sell_fail;
            case cut:
                return EvaluateState.cut;
            default:
                throw new IllegalArgumentException();
        }
    }

    public EvaluateResult buildEvaluateResult(EvaluateState2 state, StateTradesGroup group, Sign sign) {
        LocalDateTime buyLimitTime = getBuyLimitTime(sign.getStartTime(), sign.getValidateMin());

        TradeCoin activeTrade = null;
        BigDecimal earningPrice = null;

        switch (state) {
            case sell_success:
                activeTrade = tradesDataRepository.findEndTrade(sign.getCryptoCurrencyId(), sign.getStartTime(), sign.getEndTime());
                earningPrice = sign.getTargetPrice();
                break;
            case sell_success2:
                activeTrade = tradesDataRepository.findEndTrade(sign.getCryptoCurrencyId(), sign.getStartTime(), sign.getEndTime());
                earningPrice = sign.getTargetPrice2();
                break;
            case sell_success3:
                activeTrade = group.getSellTrade3();
                earningPrice = sign.getTargetPrice3();
                break;
            case sell_fail:
                activeTrade = tradesDataRepository.findEndTrade(sign.getCryptoCurrencyId(), sign.getStartTime(), sign.getEndTime());
                earningPrice = activeTrade.getTrade_price();
                break;
            case cut:
                activeTrade = group.getCutTrade();
                earningPrice = sign.getCutPrice();
                break;
            case buy_fail:
                activeTrade = null;
                earningPrice = null;
                break;
        }

        if(activeTrade != null) {
            Double earningRate = earningPrice.divide(sign.getStartPrice(), 10, BigDecimal.ROUND_CEILING)
                    .subtract(new BigDecimal("1"))
                    .multiply(new BigDecimal("100"))
                    .doubleValue();

            LocalDateTime endEarningTime = activeTrade.getTradeTimeUtc();

            TradeCoin minTrade = tradesDataRepository.findMinTrade(sign.getCryptoCurrencyId(), sign.getStartTime(), endEarningTime);
            TradeCoin maxTrade = tradesDataRepository.findMaxTrade(sign.getCryptoCurrencyId(), sign.getStartTime(), endEarningTime);

            BigDecimal minPrice = (minTrade != null)? minTrade.getTrade_price(): activeTrade.getTrade_price();
            BigDecimal maxPrice = (maxTrade != null)? maxTrade.getTrade_price(): activeTrade.getTrade_price();
            BigDecimal endPrice = activeTrade.getTrade_price();

            return EvaluateResult.builder()
                    .evaluateState(getEvaluateStateBy2(state))
                    .evaluateState2(state)
                    .earningPrice(earningPrice)
                    .earningRate(earningRate)
                    .minPrice(minPrice)
                    .maxPrice(maxPrice)
                    .endPrice(endPrice)
                    .slideNo(getSlideNo(sign.getMentorPageId()))
                    .endEarningTime(endEarningTime)
                    .build();
        } else if(state == EvaluateState2.buy_fail){
            TradeCoin endTrade = tradesDataRepository.findEndTrade(sign.getCryptoCurrencyId(), sign.getStartTime(), buyLimitTime);
            TradeCoin minTrade = tradesDataRepository.findMinTrade(sign.getCryptoCurrencyId(), sign.getStartTime(), buyLimitTime);
            TradeCoin maxTrade = tradesDataRepository.findMaxTrade(sign.getCryptoCurrencyId(), sign.getStartTime(), buyLimitTime);

            if (endTrade == null && minTrade == null && maxTrade == null) { // 해당 기간동안 거래 데이터가 없어 매수 실패한 케이스
                minTrade = maxTrade = endTrade = tradesDataRepository.findEndTrade(sign.getCryptoCurrencyId(), LocalDateTime.parse("2022-01-01T00:00:00"), sign.getStartTime());
            }

            BigDecimal minPrice = minTrade.getTrade_price();
            BigDecimal maxPrice = maxTrade.getTrade_price();
            BigDecimal endPrice = endTrade.getTrade_price();

            return EvaluateResult.builder()
                    .evaluateState(getEvaluateStateBy2(state))
                    .evaluateState2(state)
                    .earningPrice(null)
                    .earningRate(null)
                    .minPrice(minPrice)
                    .maxPrice(maxPrice)
                    .endPrice(endPrice)
                    .endEarningTime(buyLimitTime)
                    .slideNo(getSlideNo(sign.getMentorPageId()))
                    .build();
        } else { // 종료되지 않음
            return EvaluateResult.builder()
                    .evaluateState(getEvaluateStateBy2(state))
                    .evaluateState2(state)
                    .build();
        }
    }


    public JudgeEndState judgeEnd(TradeCoin buyTrade, TradeCoin endNextTrade, TradeCoin buyNextTrade) {
        if(buyTrade != null) {
            if(endNextTrade != null) {
                return JudgeEndState.end;
            } else {
                return JudgeEndState.sell_wait;
            }
        } else {
            if(buyNextTrade != null) {
                return JudgeEndState.buy_fail;
            } else {
                return JudgeEndState.buy_wait;
            }
        }
    }

    /**
     *
     * @param group
     * @return 종료상태를 가정
     */
    public JudgeState judgeCutTime(StateTradesGroup group) {
        LocalDateTime cutTime = group.getCutTrade() != null? group.getCutTrade().getTradeTimeUtc(): null;
        LocalDateTime sellTime = group.getSellTrade() != null? group.getSellTrade().getTradeTimeUtc(): null;
        LocalDateTime sellTime2 = group.getSellTrade2() != null? group.getSellTrade2().getTradeTimeUtc(): null;
        LocalDateTime sellTime3 = group.getSellTrade3() != null? group.getSellTrade3().getTradeTimeUtc(): null;

        if(cutTime != null) {
            return judgeCutTime(cutTime, sellTime, sellTime2, sellTime3);
        } else {
            return judgeTime(sellTime, sellTime2, sellTime3);
        }
    }

    /**
     *
     * @param cutTime required. buyTime after
     * @param sellTime
     * @param sellTime2
     * @param sellTime3
     * @return 종료상태를 가정 cut, sell_success, sell_success2, sell_success3
     */
    public JudgeState judgeCutTime(LocalDateTime cutTime, LocalDateTime sellTime, LocalDateTime sellTime2, LocalDateTime sellTime3) {
        if(sellTime3 != null) {
            if(sellTime3.isBefore(cutTime)) {
                return JudgeState.target3;
            } else if(sellTime2.isBefore(cutTime)) {
                return JudgeState.target2;
            } else if(sellTime.isBefore(cutTime)) {
                return JudgeState.target;
            } else {
                return JudgeState.cut;
            }
        } else if(sellTime2 != null) {
            if(sellTime2.isBefore(cutTime)) {
                return JudgeState.target2;
            } else if(sellTime.isBefore(cutTime)) {
                return JudgeState.target;
            } else {
                return JudgeState.cut;
            }
        } else if(sellTime != null) {
            if(sellTime.isBefore(cutTime)) {
                return JudgeState.target;
            } else {
                return JudgeState.cut;
            }
        } else {
            return JudgeState.cut;
        }
    }

    /**
     *
     * @param sellTime
     * @param sellTime2
     * @param sellTime3
     * @return 종료상태를 가정 sell_fail, sell_success, sell_success2, sell_success3
     */
    public JudgeState judgeTime(LocalDateTime sellTime, LocalDateTime sellTime2, LocalDateTime sellTime3) {
        if(sellTime3 != null) {
            return JudgeState.target3;
        } else if(sellTime2 != null) {
            return JudgeState.target2;
        } else if(sellTime != null) {
            return JudgeState.target;
        } else {
            return JudgeState.none;
        }
    }



}
