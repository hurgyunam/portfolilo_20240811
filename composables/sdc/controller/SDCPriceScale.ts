import type { IChartWidgetApi, IChartingLibraryWidget, VisiblePriceRange } from "~/public/charting_library/charting_library";


export class SDCPriceScale {
    private heightPixel: number;
    private visiblePriceRange: VisiblePriceRange;

    constructor(heightPixel: number, visiblePriceRange: VisiblePriceRange) {
        this.heightPixel = heightPixel;
        this.visiblePriceRange = visiblePriceRange;
    }

    public getTopPrice(topGapRate: number, topGapPixel: number): number {
        // 100 : topRate = heightPrice : topPrice
        // topPrice = topRate * heightPrice / 100

        const heightPrice = this.visiblePriceRange.to - this.visiblePriceRange.from;
        const topGapPrice = topGapRate * heightPrice / 100;

        // heightPixel : topGapPixel = heightPrice : topPrice
        // topPrice = topGapPixel * heightPrice / heightPixel
        const topGapPricePixel = topGapPixel * heightPrice / this.heightPixel;

        return this.visiblePriceRange.to - topGapPrice - topGapPricePixel;
    }

    public getBottomPrice(bottomGapRate: number, bottomGapPixel: number): number {
        const heightPrice = this.visiblePriceRange.to - this.visiblePriceRange.from;
        const bottomGapPrice = bottomGapRate * heightPrice / 100;

        const heightPixel = this.heightPixel;
        const bottomGapPricePixel = bottomGapPixel * heightPrice / heightPixel;

        return this.visiblePriceRange.from + bottomGapPrice + bottomGapPricePixel;
    }
}