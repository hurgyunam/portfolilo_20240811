import type { ITimeScaleApi, VisiblePriceRange, VisibleTimeRange } from "~/public/charting_library/charting_library";
import type { SDCWidget } from "./SDCWidget";
import { getTextByteCount } from "../_util";


export class SDCTimeScale {
    private timeScale: ITimeScaleApi;
    constructor(timeScale: ITimeScaleApi) {
        this.timeScale = timeScale;
    }

    public createTimeRateRange(rangeFromTimeSeconds: number, rangeToTimeSeconds: number, leftPaddingRate: number, rightPaddingRate: number, intervalSeconds: number): VisibleTimeRange {
        // barSpacingPixel : intervalSeconds = widthPixel : widthSeconds(x)
        const widthPixel = this.timeScale.width();
        const barSpacingPixel = this.timeScale.barSpacing();
        const widthSeconds = intervalSeconds * widthPixel / barSpacingPixel;

        // rightPaddingRate : rightPaddingSeconds(x) = 100 : widthSeconds
        const rightPaddingSeconds = rightPaddingRate * widthSeconds / 100;
        // leftPaddingRate : leftPaddingSeconds(x) = 100 : widthSeconds
        const leftPaddingSeconds = leftPaddingRate * widthSeconds / 100;

        // targetToTime = rangeToTimeSeconds + rightPaddingSeconds
        // targetFromTime = rangeFromTimeSeconds - leftPaddingSeconds
        const targetToTimeSeconds = rangeToTimeSeconds + rightPaddingSeconds;
        const targetFromTimeSeconds = rangeFromTimeSeconds - leftPaddingSeconds;

        return {
            from: targetFromTimeSeconds,
            to: targetToTimeSeconds,
        };
    }

    public createTimeMarginRightPixel(standardTimeSeconds: number, marginRightPixel: number, intervalSeconds: number): VisibleTimeRange {
        const marginRightSeconds = this.getSecondsByPixel(marginRightPixel, intervalSeconds);

        const widthPixel = this.timeScale.width();
        const barSpacingPixel = this.timeScale.barSpacing();
        const widthSeconds = intervalSeconds * widthPixel / barSpacingPixel;

        const targetToSeconds = standardTimeSeconds + marginRightSeconds;
        const targetFromSeconds = targetToSeconds - widthSeconds;

        return {
            from: targetFromSeconds,
            to: targetToSeconds,
        }
    }

    public createTextGridSeconds(time: number, labelWidthPixel: number, intervalSeconds: number): number {
        const labelWidthSeconds = this.getSecondsByPixel(labelWidthPixel, intervalSeconds);

        const textPositionSeconds = time - labelWidthSeconds/2;
        const textPositionGridSeconds = textPositionSeconds - textPositionSeconds % intervalSeconds;

        return textPositionGridSeconds;
    }

    private getSecondsByPixel(pixelValue: number, intervalSeconds: number): number {
        const widthPixel = this.timeScale.width();
        const barSpacingPixel = this.timeScale.barSpacing();
        const widthSeconds = intervalSeconds * widthPixel / barSpacingPixel;
        const secondsValue = pixelValue * widthSeconds / widthPixel;

        return secondsValue;
    }
}