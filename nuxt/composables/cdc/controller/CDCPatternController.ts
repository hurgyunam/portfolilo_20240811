import { delayed, getTimeStampByPointTime } from "~/composables/_tv-tech-chart/common";
import type { CDCWidget } from "./CDCWidget";
import type { IPatternButton, IPatternGraph } from "../_types";
import { CDCPattern } from "../elements/CDCPattern";
import type { VisibleTimeRange } from "~/public/charting_library/charting_library";

export class CDCPatternController {
    private patterns: CDCPattern[] = [];
    private isVisible: boolean = false;

    public clear(cdcWidget: CDCWidget) {
        if(this.isVisible) {
            this.patterns.forEach((pattern) => {
                pattern.hide(cdcWidget);
            });
        }

        this.patterns = [];
    }

    public async show(cdcWidget: CDCWidget):Promise<void> {
        this.isVisible = true;
        this.patterns.forEach(async (pattern) => {
            await this.waitUntilCandlesLoad(pattern.getStartSeconds(), cdcWidget);
            pattern.show(cdcWidget);
        });
    }

    public async waitUntilCandlesLoad(timeSeconds: number, cdcWidget: CDCWidget): Promise<void> {
        let minCandleTime = await cdcWidget.getMinTime();

        while(timeSeconds < minCandleTime) {
            await delayed(1000);
            minCandleTime = await cdcWidget.getMinTime();
        }
    }

    public hide(cdcWidget: CDCWidget) {
        this.isVisible = false;
        this.patterns.forEach((pattern) => {
            pattern.hide(cdcWidget);
        });
    }

    public async add(patternGraphs: IPatternGraph[], cdcWidget: CDCWidget): Promise<void> {
        const ids = this.patterns.map(pattern => pattern.getPatternScanId());
        const addPatterns = patternGraphs.filter(pattern => !ids.includes(pattern.patternScanId)) // 중복 제거
                .map(pattern => new CDCPattern(
                    pattern.patternScanId,
                    pattern.patternType,
                    pattern.patternName,
                    pattern.points,
                    getTimeStampByPointTime(pattern.startPointTime),
                    getTimeStampByPointTime(pattern.endPointTime),
                ))

        if(this.isVisible) {
            addPatterns.forEach(async (pattern) => {
                await this.waitUntilCandlesLoad(pattern.getStartSeconds(), cdcWidget);
                pattern.show(cdcWidget)
            });
        }

        this.patterns = [...this.patterns, ...addPatterns];
    }

    public async moveToPattern(cdcWidget: CDCWidget, patternId: number): Promise<void> {
        const pattern = this.patterns.filter(pattern => pattern.getPatternScanId() === patternId)[0];

        if(pattern) {
            await cdcWidget.moveByTimeRange(pattern.getStartSeconds(), pattern.getEndSeconds());
        }
    }

    public getButtons({from, to}: VisibleTimeRange): IPatternButton[] {
        return this.patterns.filter(pattern => {
            if(pattern.getStartSeconds() < from && from < pattern.getEndSeconds()) {
                // 왼쪽에 걸침
                return true;
            } else if(pattern.getStartSeconds() < to && to < pattern.getEndSeconds()) {
                // 오른쪽에 걸침
                return true;
            } else if(from < pattern.getStartSeconds() && pattern.getEndSeconds() < to) {
                // 내부에
                return true;
            } else {
                return false;
            }
        }).map(pattern => {
            return pattern.getPatternButton();
        })
    }

    public getMinTime(): number | null {
        if(this.patterns.length > 0) {
            let minTime: number = this.patterns[0].getStartSeconds();
    
            for(let i = 1; i < this.patterns.length; i++) {
                const time = this.patterns[i].getStartSeconds();

                if(time < minTime) {
                    minTime = time;
                }
            }

            return new Date(minTime).getTime() / 1000;
        } else {
            return null;
        }
    }
}