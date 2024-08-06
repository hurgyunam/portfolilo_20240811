import type { EInterval } from "~/composables/_tv-tech-chart/_types";
import { COLOR_CHART_BG, createChart, createChartReadyPromise } from "~/composables/_tv-tech-chart/common";
import type { EntityId, IChartWidgetApi, IChartingLibraryWidget, IPositionLineAdapter, IPriceScaleApi, ITimeScaleApi, ResolutionString, ShapePoint, VisiblePriceRange, VisibleTimeRange } from "~/public/charting_library/charting_library";
import { SDCScaler } from "./SDCScaler";
import { SDCScaleController } from "./SDCScaleController";
import { SDCTimeScale } from "./SDCTimeScale";
import type { ClassSignDetailInfo } from "~/composables/sign-detail/service/_types";


export class SDCWidget {
    private widget: IChartingLibraryWidget;
    constructor(cryptoId: number, interval: EInterval, container: HTMLElement, datafeedUrl: string | null) {
        this.widget = createChart(cryptoId, interval, container, datafeedUrl ?? undefined);

        this.widget.applyOverrides({
            'scalesProperties.showSeriesLastValue': false,
            'scalesProperties.lineColor': COLOR_CHART_BG,
        })
    }

    private async getChart(): Promise<IChartWidgetApi> {
        return createChartReadyPromise<IChartWidgetApi>(async (chart) => {
            return chart;
        }, this.widget);
    }

    public async createPositionLine(): Promise<IPositionLineAdapter> {
        const chart = await this.getChart();

        const positionLine = chart.createPositionLine();

        return positionLine;
    }

    public async createTrendLine(points: ShapePoint[], color: string, rightEnd: 0 | 1 | 2): Promise<EntityId | null> {
        const chart = await this.getChart();

        return chart.createMultipointShape(points, {
            shape: 'trend_line',
            lock: true,
            overrides: {
                'linestyle': 0,
                'linewidth': 2, 
                'linecolor': color,
                'rightEnd': rightEnd
            }
        })
    }

    public async createText(time: number, price: number, text: string, fontSize: number): Promise<EntityId | null> {
        const chart = await this.getChart();

        return chart.createMultipointShape([
            {time, price},
        ], {
            shape: 'text',
            text,
            zOrder: "top",
            lock: true,
            overrides: {
                fillBackground: true,
                backgroundColor: "#FFFFFF",
                drawBorder: true,
                borderColor: "#000000",
                color: "#000000",
                fontsize: fontSize,
            }
        })
    }

    public async getTimeScale(): Promise<ITimeScaleApi> {
        const chart = await this.getChart();
        
        return chart.getTimeScale();
    }

    // public async getScaler2(): Promise<SDCScaler> {
    //     const chart = await this.getChart();

    //     return new SDCScaler(chart);
    // }

    public async getScaler(signProfile: ClassSignDetailInfo, isEnd: boolean): Promise<SDCScaleController> {
        const chart = await this.getChart();

        return new SDCScaleController(
            new SDCScaler(chart),
            new SDCTimeScale(chart.getTimeScale()),
            signProfile,
            isEnd,
        );
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

    public async removeShapes (entityIds: EntityId[]): Promise<void> {
        const chart = await this.getChart();

        entityIds.filter((entityId) => {
            chart.removeEntity(entityId);
        })
    }

    public async getHeightPixel(): Promise<number | null> {
        const chart = await this.getChart();

        const heights = chart.getAllPanesHeight();

        if(heights.length > 0) {
            return heights[0];
        } else {
            return null;
        }
    }

}