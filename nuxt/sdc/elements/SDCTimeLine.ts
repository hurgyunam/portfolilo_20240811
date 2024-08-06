import type { EntityId, VisiblePriceRange } from "~/public/charting_library/charting_library";
import type { SDCWidget } from "../controller/SDCWidget";
import { COLOR_DARK_2, TIME_LABEL } from "~/composables/_tv-tech-chart/common";
import { SDCTimeScale } from "../controller/SDCTimeScale";
import { getTextByteCount } from "../_util";
import type { ISDCElement } from "./ISDCElement";
import type { SDCPriceScale } from "../controller/SDCPriceScale";

export class SDCTimeLine implements ISDCElement {
    private startSeconds: number;
    private endSeconds: number | null;
    private entityIds: EntityId[];
    private isShort: boolean;

    constructor(startTime: Date, endTime: Date | null, isShort: boolean) {
        this.startSeconds = startTime.getUtcTsSeconds();
        this.endSeconds = endTime !== null? endTime.getUtcTsSeconds() : null;
        this.entityIds = [];
        this.isShort = isShort;
    }

    public async show(widget: SDCWidget, intervalSeconds: number, priceScale: SDCPriceScale): Promise<void> {
        if(this.entityIds.length === 0) {
            const labelPrice = this.isShort? priceScale.getBottomPrice(3, TIME_LABEL) : priceScale.getTopPrice(3, 0);

            this.showLine(
                widget,
                "예측 시작",
                this.startSeconds,
                labelPrice,
                intervalSeconds,
                TIME_LABEL
            )

            if(this.endSeconds !== null) {
                this.showLine(
                    widget,
                    "예측 종료",
                    this.endSeconds,
                    labelPrice,
                    intervalSeconds,
                    TIME_LABEL
                )
            }
        } else {
            console.error(`SDCTimeLine entities lengths are not zero: ${this.entityIds.length}`)
        }
    }

    private async showLine(widget: SDCWidget, text: string, time: number, price: number, intervalSeconds: number, fontSize: number) {
        const line = await widget.createTrendLine([
            {time, price: 0},
            {time, price: 999999999}
        ], COLOR_DARK_2, 0);

        if(line) {
            this.entityIds.push(line);
        }

        const timeScale = await widget.getTimeScale();
        const sdcTimeScale = new SDCTimeScale(timeScale);

        const labelWidthPixel = getTextByteCount(text) * fontSize / 2;
        const labelSeconds = await sdcTimeScale.createTextGridSeconds(time, labelWidthPixel, intervalSeconds);

        const label = await widget.createText(
            labelSeconds,
            price,
            text,
            fontSize,
        );

        if(label) {
            this.entityIds.push(label);
        }
    }

    public async hide(widget: SDCWidget) {
        widget.removeShapes(this.entityIds);
        this.entityIds = [];
    }
}