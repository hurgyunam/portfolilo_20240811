import type { BasicRes } from "~/composables/_api/_types";
import type { EInterval } from "~/composables/_tv-tech-chart/_types";
import { getMinuteValueByInterval } from "~/composables/_tv-tech-chart/common";
import { RecentPattern, type IPatternGraph, type IPatternScan, type IRecentPattern, type PatternStatType } from "./_types";


export interface ICDCFetch {
    fetchPatternsByRange: (cryptoId: number, from: number, to: number, interval: EInterval) => Promise<IPatternScan | null>;
    fetchAroundPatternsByPatternId: (patternStatType: PatternStatType, patternId: number, windowCandleCount?: number) => Promise<IPatternScan | null>;
    fetchStats: (patternStatType: PatternStatType, patternId: number) => Promise<IPatternScan | null>;
    getRecentPattern: (cryptoId: number) => Promise<RecentPattern[]>;
}


export function useFetchCDCPattern(): ICDCFetch {
    const { $serviceContentApiFetch } = useNuxtApp();

    const fetchPatternsByRange = async (cryptoId: number, from: number, to: number, interval: EInterval): Promise<IPatternScan | null> => {
        try {
            const minuteValue = getMinuteValueByInterval(interval);
            const { data } = await $serviceContentApiFetch<BasicRes>(`pattern-scan/scroll/${cryptoId}/${minuteValue}?gteTime=${from.toFixed()}&lteTime=${to.toFixed()}`);

            const { patternScan }: { patternScan: IPatternScan } = data;

            return patternScan;
        } catch (err) {
            return null;
        }
    }

    const fetchAroundPatternsByPatternId = async (patternStatType: PatternStatType, patternId: number, windowCandleCount: number = 70): Promise<IPatternScan | null> => {
        try {
            const { data } = await $serviceContentApiFetch<BasicRes>(`pattern-scan/path/${patternStatType}/${patternId}/${windowCandleCount * 2}`);

            const { patternScan }: { patternScan: IPatternScan } = data;

            return patternScan;
        } catch (err) {
            return null;
        }
    }

    const fetchStats = async (patternStatType: PatternStatType, patternId: number): Promise<IPatternScan | null> => {
        try {
            const { data } = await $serviceContentApiFetch<BasicRes>(`pattern-scan/${patternStatType}/${patternId}`);

            const { patternScan }: { patternScan: IPatternScan } = data;

            return patternScan;
        } catch (err) {
            return null;
        }
    }

    const getRecentPattern = async (cryptoId: number): Promise<RecentPattern[]> => {
        try {
            const { data } = await $serviceContentApiFetch<BasicRes>(`pattern-scan/recent/${cryptoId}`);

            const { patternScan } : { patternScan: IRecentPattern[] } = data;

            return patternScan.map(pattern => new RecentPattern(pattern));
        } catch (err) {
            return [];
        }
    }


    return {
        fetchPatternsByRange,
        fetchAroundPatternsByPatternId,
        fetchStats,
        getRecentPattern,
    }
}