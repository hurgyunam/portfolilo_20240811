import { getIntervalByMinuteValue, getTextByInterval, type IntervalText } from "~/composables/_tv-tech-chart/common";
import type { TradeExchangeType } from "~/composables/home/service/_types";
import type { CreateMultipointShapeOptions, ShapePoint, SupportedLineTools } from "~/public/charting_library/charting_library";


export interface CryptoDetailInfo {
    cryptoCurrencyItem: {
        cryptoCurrencyId: number;
        cryptoFullCode: string;
        digitalAssetsCode: string;
        digitalAssetsEnName: string;
        digitalAssetsId: number;
        digitalAssetsKoName: string;
    }
    exchange: TradeExchangeType;
}

export interface ICandlePatternButton {
    id: number;
    name: string;
}

export interface Point {
    time: number;
    // price: number;
    price: string;
}

export interface AuxiliaryLinesRes {
    points: Point[];
}

export interface PatternItemRes {
    patternId: string;
    patternCode: string;
    patternName: string;
    symbol: string;
    state: "creating" | "success" | "fail";
    points: Point[];
    auxiliaryShape: string;
    auxiliaryLines: AuxiliaryLinesRes[];
    startPointTime: number;
    endPointTime: number;
}

export interface MultipointShape {
    points: ShapePoint[];
    options: CreateMultipointShapeOptions<object>;
}

const constHeadAndShoulders = ["HEAD_AND_SHOULDERS", "REVERSE_HEAD_AND_SHOULDERS"] as const;
const constPenants = ["BULLISH_PENNANT", "BEARISH_PENNANT"] as const;
const constTriangles = ["RISING_WEDGE", "FALLING_WEDGE", "ASCENDING_TRIANGLE", "DESCENDING_TRIANGLE", "BULLISH_TRIANGLE", "BEARISH_TRIANGLE"] as const;
const constXabcds = ["DOUBLE_TOP", "DOUBLE_BOTTOM"] as const;
const constFlags = ["BULL_FLAG", "BEAR_FLAG"] as const;

export const headAndShoulders: string[] = [...constHeadAndShoulders];
export const penants: string[] = [...constPenants];
export const triangles: string[] = [...constTriangles];
export const xabcds: string[] = [...constXabcds];
export const flags: string[] = [...constFlags];

export declare type PatternTypeHeadAndShoulder = typeof constHeadAndShoulders[number];
export declare type PatternTypePenant = typeof constPenants[number];
export declare type PatternTypeTriangle = typeof constTriangles[number];
export declare type PatternTypeXabcd = typeof constXabcds[number];
export declare type PatternTypeFlag = typeof constFlags[number]; // deprecated

export declare type PatternType = PatternTypeHeadAndShoulder | PatternTypePenant | 
            PatternTypeTriangle | PatternTypeTriangle | PatternTypeFlag;


export declare type PatternStatType = "crypto_currency" | "total";

export interface IPatternGraph {
    patternScanId: number;
    cryptoCurrencyId: number;
    patternType: PatternType;
    points: Point[];
    auxiliaryLines: AuxiliaryLinesRes[];
    auxiliaryShape: SupportedLineTools;
    startPointTime: string;
    endPointTime: string;
    intervalTime: number;
    patternName: string;
}

export declare type PatternStatIntervalType = "intervalTotal" | "interval15Min" | "interval1Hour" | "interval4Hour" | "interval1Day" | "interval7Day";

export interface IPatternStatInterval {
    intervalStatisticsId: number;
    patternStatisticsId: number;
    intervalType: PatternStatIntervalType;
    intervalCount: number;
    intervalUpRate: number;
    intervalDownRate: number;
}

export interface IPatternStatInfoItem {
    cryptoCurrencyId: number;
    digitalAssetsKoName: string;
    patternName: string;

    intervalData: IPatternStatInterval[];
    intervalTime: number;

    standardDate: string;
    startPointTime: string;
    endPointTime: string;
}

export interface IPatternScan {
    patternGraphItems: IPatternGraph[];
    patternStatisticsInfoItem: IPatternStatInfoItem;
}

export interface IPatternButton {
    patternScanId: number;
    patternName: string;
}

declare type PatternUpDownStatus = "UP" | "DOWN";

export interface IRecentPattern {
    endPointTime: string;
    intervalTime: number;
    patternName: string;
    patternScanId: number;
    patternType: PatternType;
    patternUpDownStatus: PatternUpDownStatus;
}

export class RecentPattern {
    patternName: string;
    patternScanId: number;
    patternType: PatternType;
    patternUpDownStatus: PatternUpDownStatus;

    intervalTimeText: IntervalText;
    patternTimeText: string;

    constructor(res: IRecentPattern) {
        this.patternTimeText = new Date(res.endPointTime).format("M월 dd일");
        this.intervalTimeText = getTextByInterval(getIntervalByMinuteValue(res.intervalTime));
        this.patternName = res.patternName;
        this.patternScanId = res.patternScanId;
        this.patternType = res.patternType;
        this.patternUpDownStatus = res.patternUpDownStatus;
    }

}