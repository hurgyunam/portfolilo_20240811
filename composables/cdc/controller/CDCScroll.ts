import type { EInterval } from "~/composables/_tv-tech-chart/_types";
import type { VisibleTimeRange } from "~/public/charting_library/charting_library";
import type { ICDCFetch } from "../useFetchCDCPattern";
import type { IPatternButton } from "../_types";
import type { CDCPatternController } from "./CDCPatternController";
import type { CDCWidget } from "./CDCWidget";


export class CDCScroll {
    private lastSearchedTime: number | null = null;
    private isLoading: boolean = false;
    private cdcPattern: CDCPatternController;
    private cdcFetch: ICDCFetch;

    constructor(cdcPattern: CDCPatternController, cdcFetch: ICDCFetch) {
        this.cdcPattern = cdcPattern;
        this.cdcFetch = cdcFetch;
    }

    public async onVisibleTimeRangeChanged(visibleTimeRange: VisibleTimeRange, cryptoId: number, interval: EInterval, cdcWidget: CDCWidget): Promise<IPatternButton[] | null> {
        if(this.isLoading === false) {
            this.isLoading = true;

            const from = visibleTimeRange.from - (visibleTimeRange.to - visibleTimeRange.from)/2;

            const to = (this.lastSearchedTime === null)? Date.now() / 1000 : // 최초 탐색
                    (this.lastSearchedTime > from)? (await this.cdcPattern.getMinTime()) ?? this.lastSearchedTime : // 추가 탐색
                    null;

            if(to !== null) {
                const pattern = await this.cdcFetch.fetchPatternsByRange(cryptoId, from, to, interval);

                if(pattern) {
                    await this.cdcPattern.add(pattern.patternGraphItems, cdcWidget);
                }

                this.lastSearchedTime = from;
            }

            const buttons = await this.cdcPattern.getButtons(visibleTimeRange);
            this.isLoading = false;

            return buttons;

        } else {
            return null;
        }
    }
}