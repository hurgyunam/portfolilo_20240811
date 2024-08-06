import { EInterval, type TInterval } from "~/composables/_tv-tech-chart/_types";
import type { ChartPropertiesOverrides, ChartingLibraryWidgetOptions, IChartWidgetApi, IChartingLibraryWidget, TradingTerminalWidgetOptions, VisiblePriceRange } from "~/public/charting_library/charting_library";
import { defaultOption, features_options, loadOptions, overrides } from "./widgetOptions";

export const COLOR_CHART_BG = "#F4F6F9", COLOR_LONG = "#00AF85", COLOR_SHORT = "#F3475D", COLOR_GUIDELINE_GRAY = "#BCBEC2", 
    COLOR_BLACK = "#000000", COLOR_WHITE = "#FFFFFF", COLOR_CUTPRICE = "#C8835C", COLOR_GRAY = "#D9DCE1",
    COLOR_LONG_1 = "#61CBB1", COLOR_LONG_2 = "#00B287", COLOR_LONG_3 = "#22866E",
    COLOR_SHORT_1 = "#FFA4A7", COLOR_SHORT_2 = "#F67789", COLOR_SHORT_3 = "#F3475D",
    COLOR_DARK_2 = "#707A8A"
    ;
export const fontSize = 14;

export const createChart = (cryptoId: number, interval: TInterval, container: HTMLElement, datafeedUrl?: string): IChartingLibraryWidget => {
    if(datafeedUrl === undefined) {
        const config = useRuntimeConfig();
        datafeedUrl = `${config.public.CONTENT_API_URL}/chart`;
    }

    const widgetOptions: ChartingLibraryWidgetOptions | TradingTerminalWidgetOptions = {
        datafeed: new window.Datafeeds.UDFCompatibleDatafeed(datafeedUrl),
        container: container,
        symbol: `${cryptoId}`,
        interval,
        ...defaultOption,
        ...loadOptions as any,
        ...features_options,
        ...overrides,
    }

    const { $widget } = useNuxtApp();
    const widget = new $widget(widgetOptions);

    const option: Partial<ChartPropertiesOverrides> = {
        "paneProperties.background": COLOR_CHART_BG,
        "paneProperties.vertGridProperties.color": COLOR_CHART_BG,
        "paneProperties.horzGridProperties.color": COLOR_CHART_BG,

        "scalesProperties.fontSize": 12,
        'scalesProperties.showSeriesLastValue': false,
        'scalesProperties.showStudyLastValue': false,

        'linetoolposition.bodyFontBold': false,
        'linetoolposition.quantityFontBold': false,
        'linetoolposition.bodyFontFamily': 'SpoqaHanSansNeo',
        'linetoolposition.quantityFontFamily': 'SpoqaHanSansNeo',
        'linetoolposition.bodyFontSize': fontSize,
        'linetoolposition.quantityFontSize': fontSize,
        'linetoolposition.showDateTimeRange': true,
    }

    widget.applyOverrides(option);

    return widget;
}


export declare type OnChartReadyCallback<T> = ((chart: IChartWidgetApi) => Promise<T>);

export const createChartReadyPromise = <T>(onReady: OnChartReadyCallback<T>, widget: IChartingLibraryWidget | null) => {
    return new Promise<T>((resolve, reject) => {
        if(widget) {
            widget.onChartReady(() => {
                const chart = widget.activeChart();

                if(chart) {
                    onReady(chart).then((result: T) => resolve(result));
                } else {
                    reject();
                }
            })
        } else {
            reject();
        }
    })
}

export const getMinuteValueByInterval = (interval: EInterval): number => {
    switch(interval) {
        case EInterval.M15:
            return 15;
        case EInterval.H1:
            return 1 * 60;
        case EInterval.H4:
            return 4 * 60;
        case EInterval.D1:
            return 1 * 24 * 60;
        case EInterval.D7:
            return 7 * 24 * 60;
        default:
            return 15;
    }
}

export const getIntervalByMinuteValue = (minuteValue: number): EInterval => {
    switch(minuteValue) {
        case 15:
            return EInterval.M15;
        case 1 * 60:
            return EInterval.H1;
        case 4 * 60:
            return EInterval.H4;
        case 1 * 24 * 60:
            return EInterval.D1;
        case 7 * 24 * 60:
            return EInterval.D7;
        default:
            return EInterval.M15;
    }
}

export interface IntervalText {
    value: EInterval;
    text: string;
}

export const createIntervalTexts = (): IntervalText[] => {
    return Object.values(EInterval).map((interval) => {
        return getTextByInterval(interval);
    });
}

export const getTextByInterval = (interval: EInterval): IntervalText => {
    return {
        value: interval,
        text: interval.endsWith("H")? interval.replace("H", "시간") :
            interval.endsWith("D") ? interval.replace("D", "일") :
            interval + "분"
    }
}

export const getTimeStampByPointTime = (pointTime: string) => {
    return Date.parseUtcText(pointTime).getTime() / 1000;
}

export const delayed = (milliseconds: number): Promise<void> => {
    return new Promise((resolve, reject) => {
        setTimeout(() => {
            resolve();
        }, milliseconds);
    })
}