import { EInterval } from "~/composables/_tv-tech-chart/_types";
import type { PatternStatIntervalType } from "./_types";



export const getIntervalByPatternStatType = (type: PatternStatIntervalType): EInterval | undefined => {
    switch(type) {
        case "interval15Min":
            return EInterval.M15;
        case "interval1Hour":
            return EInterval.H1;
        case "interval4Hour":
            return EInterval.H4;
        case "interval1Day":
            return EInterval.D1;
        case "interval7Day":
            return EInterval.D7;
        default:
            return undefined;
    }
}

export const getTextPatternStatType = (type: PatternStatIntervalType): String => {
    switch(type) {
        case "interval15Min":
            return "15분";
        case "interval1Hour":
            return "1시간";
        case "interval4Hour":
            return "4시간";
        case "interval1Day":
            return "1일";
        case "interval7Day":
            return "7일";
        default:
            return "";
    }
}

export const getPatternStatTypeByInterval = (interval: EInterval): PatternStatIntervalType | undefined => {
    switch(interval) {
        case EInterval.M15:
            return "interval15Min";
        case EInterval.H1:
            return "interval1Hour";
        case EInterval.H4:
            return "interval4Hour";
        case EInterval.D1:
            return "interval1Day";
        case EInterval.D7:
            return "interval7Day";
        default:
            return undefined;
    }
}