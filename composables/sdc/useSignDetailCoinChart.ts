import { EInterval } from "~/composables/_tv-tech-chart/_types";
import type { ClassSignDetailInfo } from "~/composables/sign-detail/service/_types";
import { getMinuteValueByInterval } from "~/composables/_tv-tech-chart/common";
import type { VisibleTimeRange } from "~/public/charting_library/charting_library";
import { SDCTimeLine } from "./elements/SDCTimeLine";
import { SDCController } from "./controller/SDCController";
import { SDCPricePositionSignGroup } from "./elements/SDCPricePositionSignGroup";
import { useInstance } from "~/composables/_api/useInstance";

const getIntervalByValidateMinute = (validateMin: number): EInterval => {
    if(validateMin <= 30) {
        return EInterval.M1;
    } if(validateMin <= 6 * 60) {
        return EInterval.M5;
    } else if(validateMin <= 24 * 60) {
        return EInterval.M15;
    } else {
        return EInterval.H1;
    }
}

const getIntervalByStartEndTime = (startTimeText: string, endTimeText: string): EInterval => {
    const startTime = Date.parseUtcToLocal(startTimeText);
    const endTime = Date.parseUtcToLocal(endTimeText);

    return getIntervalByValidateMinute((endTime.getTime() - startTime.getTime()) / 1000 / 60);
}

const getFractionalDigits = (signProfile: ClassSignDetailInfo): number => {
    const { numberFormat } = useInstance();
    
    const prices = [signProfile.startPrice, 
        signProfile.targetPrice, signProfile.targetPrice2, signProfile.targetPrice3, 
        signProfile.cutPrice];

    const remainders = prices.map(price => price % 1);

    if(remainders.filter(remainder => remainder > 0).length === 0) {
        return 0;
    } else {
        const fractionaDigits = prices.map(price => numberFormat(price).toString())
            .map(priceText => priceText.length - priceText.indexOf(".") - 1);

        return Math.max(...fractionaDigits);
    }
}

export function useSignDetailCoinChart(signProfile: ClassSignDetailInfo) {
    const tvChartContainerRef = ref<HTMLElement | null>(null);
    const sdcControllerRef = ref<SDCController | null>(null);
    // const signDetailCoinChartLine = ref<SignDetailCoinChartLine | null>(null);

    const secondsValueRef = computed(() => getMinuteValueByInterval(intervalRef.value) * 60);
    const visiblePositionRef = ref<boolean>(true);
    const isEnd = ["sell_fail", "sell_success", "sell_success2", "sell_success3", "buy_fail", "cut"].includes(signProfile.evaluateState);
    const intervalRef = ref<EInterval>(isEnd? getIntervalByStartEndTime(signProfile.createdAt, signProfile.endEarningTime) : getIntervalByValidateMinute(signProfile.validateMin));

    const sdcTimeLine = new SDCTimeLine(new Date(signProfile.createdAt), isEnd? new Date(signProfile.endEarningTime) : null, signProfile.positionType === "SHORT");
    const sdcPricePositionSignGroup = new SDCPricePositionSignGroup(signProfile);

    const fractionaDigits = getFractionalDigits(signProfile);

    onMounted(async () => {
        if(tvChartContainerRef.value) {
            const candleMinTs = new Date(signProfile.createdAt).getUtcTsSeconds();
            const candleMaxTs = new Date(signProfile.endEarningTime).getUtcTsSeconds();
            const nowSecondsTs = new Date().getUtcTsSeconds();

            const config = useRuntimeConfig();
            const datafeedUrl = isEnd? 
                `${config.public.CONTENT_API_URL}/chart/${candleMinTs}/${candleMaxTs}/${fractionaDigits}`
                : `${config.public.CONTENT_API_URL}/chart/${candleMinTs}/${nowSecondsTs}/${fractionaDigits}`;

            sdcControllerRef.value = new SDCController(
                signProfile.cryptoId,
                intervalRef.value,
                tvChartContainerRef.value,
                datafeedUrl,
            );

            // sdcControllerRef.value.setVisiblePriceRange(
            //     signProfile.positionType === "SHORT"? signProfile.targetPrice3 : signProfile.startPrice,
            //     signProfile.positionType === "SHORT"? signProfile.startPrice : signProfile.targetPrice3,
            //     10
            // );

            // await sdcControllerRef.value.setVisibleTimeRange(signProfile, secondsValueRef.value);

            await sdcControllerRef.value.updateScale(signProfile, secondsValueRef.value);

            await sdcControllerRef.value.show(sdcTimeLine, secondsValueRef.value);
            await sdcControllerRef.value.show(sdcPricePositionSignGroup, secondsValueRef.value);
        }
    })

    const onToggleEarningRateButton = async () => { // 수익률 눈동자
        visiblePositionRef.value = !visiblePositionRef.value;

        if(visiblePositionRef.value) {
            sdcControllerRef.value?.show(sdcPricePositionSignGroup, secondsValueRef.value);
        } else {
            sdcControllerRef.value?.hide(sdcPricePositionSignGroup);
        }

        if(sdcControllerRef.value) {
            sdcControllerRef.value.updateScale(signProfile, secondsValueRef.value);
        }
    }
    
    const onClickIntervalButton =  async (interval: EInterval): Promise<void> => {
        intervalRef.value = interval;

        if(sdcControllerRef.value) {
            await sdcControllerRef.value.setInterval(interval);
    
            await sdcControllerRef.value.updateScale(signProfile, secondsValueRef.value);
    
            await sdcControllerRef.value.hide(sdcTimeLine);
            await sdcControllerRef.value.hide(sdcPricePositionSignGroup);
            await sdcControllerRef.value.show(sdcTimeLine, secondsValueRef.value);
            await sdcControllerRef.value.show(sdcPricePositionSignGroup, secondsValueRef.value);
        }
    }

    return {
        tvChartContainerRef,
        onToggleEarningRateButton,
        onClickIntervalButton,
        selectedInterval: computed(() => intervalRef.value),
        visiblePosition: computed(() => visiblePositionRef.value),
    }
}