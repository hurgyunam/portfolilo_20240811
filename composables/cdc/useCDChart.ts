import { EInterval } from "~/composables/_tv-tech-chart/_types";
import { useFetchCDCPattern, type ICDCFetch } from "./useFetchCDCPattern";
import { getIntervalByMinuteValue, getMinuteValueByInterval } from "~/composables/_tv-tech-chart/common";
import type { IPatternStatInfoItem, IPatternButton, PatternStatType } from "./_types";
import type { VisibleTimeRange } from "~/public/charting_library/charting_library";
import { CDCWidget } from "./controller/CDCWidget";
import { CDCScroll } from "./controller/CDCScroll";
import { CDCPatternController } from "./controller/CDCPatternController";

export function useCDChart() {
    const cdcFetch = useFetchCDCPattern();
    let cdcWidget: CDCWidget | null = null;
    const cdcPattern = ref<CDCPatternController>();
    let cdcScroll: CDCScroll | null = null;

    const interval = ref<EInterval>(EInterval.M1);

    const { params } = useRoute();
    const buttonPatternId = ref<number | null>(typeof(params.patternid) === 'string'? parseInt(params.patternid) : null);
    const patternStatType = ref<PatternStatType>("crypto_currency");
    let cryptoCurrencyId: number | null = null;

    const symbolName = ref<string>("");
    const buttons = ref<IPatternButton[]>([]);
    const patternStat = ref<IPatternStatInfoItem>();
    
    const create = async (container: HTMLElement, cryptoId: number, patternId: number | null) => {
        cryptoCurrencyId = cryptoId;
        cdcWidget = new CDCWidget(cryptoId, interval.value, container);
        cdcPattern.value = new CDCPatternController();
        cdcScroll = new CDCScroll(cdcPattern.value, cdcFetch);

        // 분봉이 바뀌더라도 캔들 수는 크게 바뀌지 않을 것이다.
        const candleCount = await cdcWidget.getVisibleCandleCount(interval.value);

        if(patternId) {
            const pattern = await cdcFetch.fetchAroundPatternsByPatternId(patternStatType.value, patternId, candleCount);

            if(pattern) {
                const patternInterval = getIntervalByMinuteValue(pattern.patternStatisticsInfoItem.intervalTime);

                interval.value = patternInterval;
                await cdcWidget.setInterval(patternInterval);

                await cdcPattern.value.add(pattern.patternGraphItems, cdcWidget);

                await cdcPattern.value.moveToPattern(cdcWidget, patternId);

                // await cdcPattern.value.draw();
                const visibleTimeRange = await cdcWidget.getVisibleTimeRange();

                buttons.value = await cdcPattern.value.getButtons(visibleTimeRange);
                if(buttons.value.length > 0) {
                    buttonPatternId.value = buttons.value[0].patternScanId;
                }

                patternStat.value = pattern.patternStatisticsInfoItem;
            }
        }

        cdcWidget.subscribe(async (range) => {
            if(cdcScroll && cdcWidget) {
                const changedButtons = await cdcScroll.onVisibleTimeRangeChanged(range, cryptoId, interval.value, cdcWidget);

                if(changedButtons !== null) {
                    buttons.value = changedButtons;

                    const filtered = changedButtons.filter(button => button.patternScanId === buttonPatternId.value)

                    if(filtered.length === 0 && changedButtons.length > 0) {
                        buttonPatternId.value = changedButtons[0].patternScanId;
                    }

                    if(patternStat.value === undefined && changedButtons.length > 0) {
                        const pattern = await cdcFetch.fetchAroundPatternsByPatternId(patternStatType.value, changedButtons[0].patternScanId, candleCount);

                        if(pattern) {
                            patternStat.value = pattern.patternStatisticsInfoItem;
                        }
                    }

                    if(changedButtons.length === 0) {
                        patternStat.value = undefined;
                    }
                }
            }
        })
    }

    const moveBySeconds = async (seconds: number) => {
        if(cdcWidget) {
            await cdcWidget.moveBySeconds(seconds);
        }
    }

    const moveByTimeRange = async (from: number, to: number) => {
        if(cdcWidget) {
            const range = await cdcWidget.moveByTimeRange(from, to);

            if(cdcScroll && cryptoCurrencyId) {
                const changedButtons = await cdcScroll.onVisibleTimeRangeChanged(range, cryptoCurrencyId, interval.value, cdcWidget);

                if(changedButtons !== null) {
                    buttons.value = changedButtons;

                    const filtered = changedButtons.filter(button => button.patternScanId === buttonPatternId.value)

                    if(filtered.length === 0 && changedButtons.length > 0) {
                        buttonPatternId.value = changedButtons[0].patternScanId;
                    }
                }
            }
        }
    }

    const setPatternVisible = (value: boolean) => {
        if(cdcPattern.value && cdcWidget) {
            if(value) {
                cdcPattern.value.show(cdcWidget);
            } else {
                cdcPattern.value.hide(cdcWidget);
            }
        } else {
            return new Promise<void>((resolve, reject) => {
                watch(cdcPattern, (newCdcPattern) => {
                    if(newCdcPattern && cdcWidget) {
                        if(value) {
                            newCdcPattern.show(cdcWidget);
                        } else {
                            newCdcPattern.hide(cdcWidget);
                        }
                    }
                    resolve();
                })
            })
        }
    }

    const onClickButtonPatternId = async (paramButtonPatternId: number) => { // buttonPatternIdRef를 watch하면 내부에서 변경되는 케이스로 인해 에러가 발생
        if(cdcWidget && cdcPattern.value && paramButtonPatternId) {
            await cdcPattern.value.moveToPattern(cdcWidget, paramButtonPatternId);
            const stats = await cdcFetch.fetchStats(patternStatType.value, paramButtonPatternId);
            patternStat.value = stats?.patternStatisticsInfoItem;
            buttonPatternId.value = paramButtonPatternId;
        }
    }

    watch(interval, async (newValue) => {
        if(cdcWidget && cdcPattern.value) {
            cdcPattern.value.clear(cdcWidget);
            await cdcWidget.setInterval(newValue);
    
            const cryptoId = await cdcWidget.getSymbol();
            const visibleTimeRange = await cdcWidget.getVisibleTimeRange();
            const patternScan = await cdcFetch.fetchPatternsByRange(cryptoId, visibleTimeRange.from, visibleTimeRange.to, interval.value);

            if(patternScan) {
                await cdcPattern.value.add(patternScan.patternGraphItems, cdcWidget);

                const visibleTimeRange = await cdcWidget.getVisibleTimeRange();
                buttons.value = await cdcPattern.value.getButtons(visibleTimeRange);

                if(buttons.value.length > 0) {
                    buttonPatternId.value = buttons.value[0].patternScanId;

                    if(patternStatType.value && buttonPatternId.value) {
                        const stats = await cdcFetch.fetchStats(patternStatType.value, buttonPatternId.value);
                        patternStat.value = stats?.patternStatisticsInfoItem;
                    }
                } else {
                    patternStat.value = undefined;
                }
            }
        }
    })

    // watch(buttonPatternId, async (newValue) => {
    //     if(cdcWidget && cdcPattern.value && newValue) {
    //         await cdcPattern.value.moveToPattern(cdcWidget, newValue);
    //         const stats = await cdcFetch.fetchStats(patternStatType.value, newValue);
    //         patternStat.value = stats?.patternStatisticsInfoItem;
    //     }
    // })

    watch(patternStatType, async (newValue) => {
        if(buttonPatternId.value) {
            const stats = await cdcFetch.fetchStats(newValue, buttonPatternId.value);
            patternStat.value = stats?.patternStatisticsInfoItem;
        }
    })



    return {
        interval,
        symbolName,
        buttons,
        buttonPatternId,
        patternStatType,
        patternStat,
        create,
        moveBySeconds,
        moveByTimeRange,
        setPatternVisible,
        onClickButtonPatternId,
    }
}