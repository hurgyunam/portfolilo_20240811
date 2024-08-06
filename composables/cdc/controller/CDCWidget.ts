import type { EInterval } from "~/composables/_tv-tech-chart/_types";
import { createChart, createChartReadyPromise, getMinuteValueByInterval } from "~/composables/_tv-tech-chart/common";
import type { EntityId, IChartWidgetApi, IChartingLibraryWidget, ResolutionString, VisibleTimeRange } from "~/public/charting_library/charting_library";
import type { MultipointShape } from "../_types";


export class CDCWidget {
    private widget: IChartingLibraryWidget;
    constructor(cryptoId: number, interval: EInterval, container: HTMLElement) {
        this.widget = createChart(cryptoId, interval, container);
        this.init();
    }

    private async getChart(): Promise<IChartWidgetApi> {
        return createChartReadyPromise<IChartWidgetApi>(async (chart) => {
            return chart;
        }, this.widget);
    }

    private async init() {
        const chart = await this.getChart();

        chart.createStudy('volume', true);
    }

    public async getSymbol(): Promise<number> {
        const chart = await this.getChart();

        return parseInt(chart.symbol());
    }

    public async setSymbol(symbolId: number): Promise<void> {
        const chart = await this.getChart();

        return new Promise<void>((resolve, reject) => {
            chart.setSymbol(
                symbolId + '',
                {
                    dataReady: () => {
                        resolve();
                    }
                }
            );
        });
    }

    public async getVisibleTimeRange(): Promise<VisibleTimeRange> {
        const chart = await this.getChart();

        return chart.getVisibleRange();
    }

    public async setVisibleTimeRange(visibleTimeRange: VisibleTimeRange): Promise<void> {
        const chart = await this.getChart();

        return chart.setVisibleRange(visibleTimeRange);
    }

    public async createMultipointShapes(shapes: MultipointShape[]): Promise<EntityId[]>  {
        const chart = await this.getChart();

        const entityIds = shapes.map((shape) => {
            return chart.createMultipointShape(shape.points, shape.options);
        }).filter(entityId => entityId) as EntityId[];

        return entityIds;
    }

    public async removeShapes (entityIds: EntityId[]): Promise<void> {
        const chart = await this.getChart();

        entityIds.filter((entityId) => {
            chart.removeEntity(entityId);
        })
    }

    public async subscribe(onVisibleRangeChanged: (range: VisibleTimeRange) => void): Promise<void> {
        const chart = await this.getChart();

        chart.onVisibleRangeChanged().subscribe(null, onVisibleRangeChanged)
    }

    public async setInterval(interval: EInterval): Promise<void> {
        const chart = await this.getChart();
        
        return new Promise((resolve, reject) => {
            chart.setResolution(
                interval as ResolutionString,
                {
                    dataReady: () => {
                        resolve();
                    }
                }
            );
        })
    }

    public async getVisibleCandleCount(interval: EInterval): Promise<number> {
        const chart = await this.getChart();

        const {from, to} = chart.getVisibleRange();
        const minuteValue = getMinuteValueByInterval(interval);
        return (to - from) / minuteValue / 60;
    }

    public async moveBySeconds(seconds: number): Promise<void> {
        const chart = await this.getChart();

        const {from, to} = chart.getVisibleRange();
        const timeDiff = to - from;
        chart.setVisibleRange({
            from: seconds - timeDiff/2,
            to: seconds + timeDiff/2,
        })
    }

    public async moveByTimeRange(from: number, to: number):Promise<VisibleTimeRange> {
        const chart = await this.getChart();
        const visigleRange = await chart.getVisibleRange();
        let targetVisibleRange: VisibleTimeRange;

        if(to - from < visigleRange.to - visigleRange.from) { // 패턴이 현재 차트 화면보다 작다 => 현재 해상도 유지
            const remainTime = (visigleRange.to - visigleRange.from) - (to - from);

            targetVisibleRange = {from: from - remainTime / 2, to: to + remainTime / 2};
        } else {
            const bufferTime = (to - from) * 0.1;

            targetVisibleRange = {from: from - bufferTime, to: to + bufferTime};
        }

        chart.setVisibleRange(targetVisibleRange);

        return targetVisibleRange;
    }

    public async getMinTime(): Promise<number> {
        const chart = await this.getChart();
        const candles = await chart.exportData();

        const IDX_TIME = 0;

        return Math.min(...candles.data.map((candle) => candle[IDX_TIME]));
    }
}