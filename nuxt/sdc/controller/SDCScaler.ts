import type { IChartWidgetApi, VisiblePriceRange, VisibleTimeRange } from "~/public/charting_library/charting_library";

export class SDCScaler {
    private chart: IChartWidgetApi;

    constructor(chart: IChartWidgetApi) {
        this.chart = chart;
    }

    public async setVisibleTimeRange(visibleTimeRange: VisibleTimeRange): Promise<void> {
        return this.chart.setVisibleRange(visibleTimeRange);
    }

    public getVisibleTimeRange(): VisibleTimeRange {
        return this.chart.getVisibleRange();
    }

    public async setVisiblePriceRange(visiblePriceRange: VisiblePriceRange): Promise<boolean> {
        const panes = this.chart.getPanes();

        if(panes.length > 0) {
            const priceScale = panes[0].getMainSourcePriceScale();

            if(priceScale) {
                priceScale.setVisiblePriceRange(visiblePriceRange);
                return true;
            } else {
                return false;
            }
        } else {
            return false;
        }
    }

    public getVisiblePriceRange(): VisiblePriceRange | null {
        const panes = this.chart.getPanes();

        if(panes.length > 0) {
            return panes[0].getMainSourcePriceScale()?.getVisiblePriceRange() ?? null;
        } else {
            return null;
        }
    }

    public async getMinPrice(): Promise<number> {
        const data = await this.chart.exportData();

        return Math.min(...data.data.map(item => item[3]));
    }

    public async getMaxPrice(): Promise<number> {
        const data = await this.chart.exportData();

        return Math.max(...data.data.map(item => item[2]));
    }

    public async getMinTs(): Promise<number> {
        const data = await this.chart.exportData();

        return Math.min(...data.data.map(item => item[0]));
    }

    public async getMaxTs(): Promise<number> {
        const data = await this.chart.exportData();

        return Math.max(...data.data.map(item => item[0]));
    }
}