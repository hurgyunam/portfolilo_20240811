package com.lab2ai.cosign_earning_scheduler.spot.service;

import com.lab2ai.cosign_earning_scheduler.ai_model.repository.SignRepository;
import com.lab2ai.cosign_earning_scheduler.ai_model.service.MentorService;
import com.lab2ai.cosign_earning_scheduler.alarm.dto.AiSignSendRequest;
import com.lab2ai.cosign_earning_scheduler.alarm.dto.BookmarkAlarmItem;
import com.lab2ai.cosign_earning_scheduler.alarm.dto.FcmMessageItem;
import com.lab2ai.cosign_earning_scheduler.core.dto.EvaluateResult;
import com.lab2ai.cosign_earning_scheduler.core.entity.Sign;
import com.lab2ai.cosign_earning_scheduler.core.enums.EvaluateState;
import com.lab2ai.cosign_earning_scheduler.core.enums.EvaluateState2;
import com.lab2ai.cosign_earning_scheduler.core.enums.TradeType;
import com.lab2ai.cosign_earning_scheduler.spot.dto.*;
import com.lab2ai.cosign_earning_scheduler.spot.entity.SignEvaluateHistory;
import com.lab2ai.cosign_earning_scheduler.alarm.repository.BookmarkRepository;
import com.lab2ai.cosign_earning_scheduler.spot.repository.SignEvaluateHistoryRepository;
import com.lab2ai.cosign_earning_scheduler.spot.repository.TradesDataRepository;
import com.lab2ai.cosign_earning_scheduler.alarm.strategy.BookmarkAlarmStrategy;
import com.lab2ai.cosign_earning_scheduler.spot.util.FeedRestTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

/**
 * The type Sign evaluate service.
 */
@Service
public class SignEvaluateService {
    private final SignEvaluateTradeService signEvaluateTradeService;
    private final  FeedRestTemplate feedRestTemplate;
    private final  SignRepository signRepository;
    private final  MentorService mentorService;
    private final  TradesDataRepository tradesDataRepository;
    private final  SignEvaluateHistoryRepository signEvaluateHistoryRepository;
    private final BookmarkRepository bookmarkRepository;
    private  final ApplicationEventPublisher publisher;


    private static final Double STANDARD_EVALUATE_VALUE = 3.0d;
    private final List<EvaluateState2> endStates = Arrays.asList(EvaluateState2.cut, EvaluateState2.sell_fail, EvaluateState2.sell_success, EvaluateState2.sell_success2, EvaluateState2.sell_success3, EvaluateState2.buy_fail);
    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");
    private final Logger log = LoggerFactory.getLogger(this.getClass().getSimpleName());

    @Autowired
    public SignEvaluateService(SignEvaluateTradeService signEvaluateTradeService, FeedRestTemplate feedRestTemplate, SignRepository signRepository, MentorService mentorService, TradesDataRepository tradesDataRepository, SignEvaluateHistoryRepository signEvaluateHistoryRepository, BookmarkRepository bookmarkRepository, ApplicationEventPublisher publisher) {
        this.signEvaluateTradeService = signEvaluateTradeService;
        this.feedRestTemplate = feedRestTemplate;
        this.signRepository = signRepository;
        this.mentorService = mentorService;
        this.tradesDataRepository = tradesDataRepository;
        this.signEvaluateHistoryRepository = signEvaluateHistoryRepository;
        this.bookmarkRepository = bookmarkRepository;
        this.publisher = publisher;
    }

    /**
     * Evaluate.
     */
    public void evaluate() throws NoSuchAlgorithmException, KeyStoreException, KeyManagementException {

        LocalDateTime curTime = LocalDateTime.now();
        List<Sign> signs = signRepository.getProceedSigns(curTime, TradeType.SPOT); // targetState1,2가 모두 sell_wait 혹은 buy_wait을 찾는 sql문 즉, 진행중인 sign 찾기
        List<UUID> endSignIds = new ArrayList<>();
        boolean hasEnd = false;

        // 종료시간 변경 필요 => 그냥 아예 endTime 수정하면 안되나?
        for (int i = 0; i < signs.size(); i++) {
            Sign sign = signs.get(i);
            try {
                EvaluateState beforeState = sign.getEvaluateState(); // targetEarningRate1의 state

                EvaluateResult result = this.evaluateSign(sign); // 수익률 평가

                // endStates List는 EvaluateState2 상태에 대한 리스트 buy_wait, sell_wait은 포함되지 않는다. 수익이 나지 않았기 때문에
                // 아래 조건문은 sign 종료가 EvaluateState2에 됐을 때 endSignIds 리스트에 담는다. 즉, EvaluateState에 종료된 sign은 feed,feed_sign 테이블에 저장되지 않는다.
                if (endStates.contains(result.getEvaluateState2())) {
                    hasEnd = true;
                    endSignIds.add(sign.getId());
                }

                this.saveSignEvaluateHistory(sign, beforeState); // sign_evaluate_history 테이블에 저장 즉, 수익률 저장 테이블

                signRepository.updateResult(result, sign.getId());// 수익률 상태 및 수익 금액 update sql문

                // ---------------------- 북마크 로직 시작 ----------------------
                Sign afterSign = signRepository.findById(sign.getId()).orElseThrow(() -> new Exception("북마크 알람을 보내기 위해 직전 signId로 탐색 중 에러 발생"));
                if (sign.getEvaluateState2() != afterSign.getEvaluateState2()) { // 실질적으로 evaluate state가 바뀌었는지 체크 바뀌었으면 bookmark 알람을 보내야한다.
                    log.info("afterEvaluateState: {}, beforeEvaluateState: {}, signId: {}", afterSign.getEvaluateState2(), sign.getEvaluateState2(), sign.getId());
                    this.sendBookmarkAlarm(afterSign);

                    // TODO: AI State 살리기
//                    this.sendAiSignState(afterSign);
                }
                // ---------------------- 북마크 로직 종료 ----------------------
            } catch (Exception ex) {
                System.out.println("sign evaluate error " + sign.getId());
                ex.printStackTrace();
            }
        }

        if (hasEnd) {
            mentorService.evaluateMentors();
            endEvaluateSetSignList(endSignIds); //종료 된 싸인 리스트 전달
        }
    }

    /**
     * Evaluate sign evaluate result.
     *
     * @param sign the sign
     * @return the evaluate result
     */
    public EvaluateResult evaluateSign(Sign sign) {
        StateTradesGroup group = signEvaluateTradeService.buildStateTradesGroup(sign);
        EvaluateState2 state = signEvaluateTradeService.judge(group);

        EvaluateResult result = signEvaluateTradeService.buildEvaluateResult(state, group, sign);

        if (sign.getTargetPrice2() == null) {
            result.setEvaluateState2(null);
        }

        signRepository.updateResult(result, sign.getId());

        return result;
    }

    /**
     * 종료 된 싸인 리스트를 받아서 id를 추출하고 해당 싸인 리스트 조회
     * 조회 된 싸인 리스트 데이터를 가지고 특정 수익률이 넘은 싸인만 피드로 전달
     *
     * @param signIds the signs ids
     */
    public void endEvaluateSetSignList(List<UUID> signIds) {
        List<Sign> endSigns = signRepository.findSignBySignIds(signIds);
        if (!endSigns.isEmpty()) {
            // 수익률(earning_rate)이 3.0% 넘으면 feed,feed_sign 테이블에 저장한다.
            endSigns.stream()
                    .filter(sign -> sign.getEarningRate() != null)
                    .filter(sign -> sign.getEarningRate() >= STANDARD_EVALUATE_VALUE)
                    .forEach(sign -> {
                        try {
                            feedRestTemplate.sendFeedSign(sign);
                            log.info("수익률이 3% 이상인 sign은 피드에 저장됩니다. signId = {}", sign.getId());
                        } catch (NoSuchAlgorithmException | KeyStoreException | KeyManagementException |
                                 NullPointerException e) {
                            e.printStackTrace();
                            log.info("에러 발생!!!!!!!! signId={}", sign);
                            throw new RuntimeException(e);
                        }
                    });
        }
    }
}
