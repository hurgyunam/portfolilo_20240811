import type { IPositionLineAdapter, VisiblePriceRange } from "~/public/charting_library/charting_library";
import type { SDCWidget } from "../controller/SDCWidget";
import type { IPositionParam } from "../_types";
import { COLOR_CHART_BG } from "~/composables/_tv-tech-chart/common";
import type { ISDCElement } from "./ISDCElement";
import type { SDCPriceScale } from "../controller/SDCPriceScale";


export class SDCPricePosition implements ISDCElement {
    private param: IPositionParam;
    private positionLine: IPositionLineAdapter | null;

    constructor(param: IPositionParam) {
        this.param = param;
        this.positionLine = null;
    }

    public async show(widget: SDCWidget, intervalSeconds: number, priceScale: SDCPriceScale): Promise<void> {
        this.positionLine = await widget.createPositionLine();

        this.positionLine.setPrice(this.param.price)
            .setLineColor(this.param.lineColor ?? COLOR_CHART_BG)
            .setExtendLeft(false)
        ;

        if(this.param.label) {
            this.positionLine.setText(this.param.label);
        } else {
            this.positionLine.setText("");
        }

        if(this.param.labelBgColor && this.param.labelTextColor) {
            this.positionLine
                .setBodyBackgroundColor(this.param.labelBgColor)
                .setBodyTextColor(this.param.labelTextColor)
        }

        if(this.param.rate && this.param.rateBgColor && this.param.rateTextColor) {
            this.positionLine
                .setQuantity(`${this.param.rate.toFixed(2)}%`)
                .setQuantityBackgroundColor(this.param.rateBgColor)
                .setQuantityTextColor(this.param.rateTextColor)
            ;
        } else {
            this.positionLine.setQuantity("");
        }

        if(this.param.borderColor) {
            this.positionLine
                .setBodyBorderColor(this.param.borderColor)
                .setQuantityBorderColor(this.param.borderColor)
            ;
        } else {
            this.positionLine
                .setBodyBorderColor(COLOR_CHART_BG)
                .setQuantityBorderColor(COLOR_CHART_BG)
            ;
        }

        // const textByteCount = getTextByteCount(
        //     `${this.positionLine.getText()}${this.positionLine.getQuantity()}`
        // ) + ((this.positionLine.getQuantity().length > 0)? 2: 0);

        const timeScale = await widget.getTimeScale();
        const chartWidth = timeScale.width();
        // console.log("hey", chartWidth);
        // const labelWidthRate = 100 * textByteCount / 2 * fontSize / chartWidth;
        // const marginRight = 100 - (this.param.marginLeft??0) - labelWidthRate;
        let marginRightPercentage: number = 0;

        if(this.param.marginRightPixel) {
            marginRightPercentage = 100 * this.param.marginRightPixel / chartWidth;
        }

        this.positionLine
            .setLineLength(marginRightPercentage)
            ;
    }

    public async hide(widget: SDCWidget): Promise<void> {
        this.positionLine?.remove();
    }
}