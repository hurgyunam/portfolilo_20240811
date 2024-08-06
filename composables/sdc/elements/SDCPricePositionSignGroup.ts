import type { VisiblePriceRange } from "~/public/charting_library/charting_library";
import type { ClassSignDetailInfo } from "../../../service/_types";
import { SDCPricePosition } from "./SDCPricePosition";
import type { SDCWidget } from "../controller/SDCWidget";
import { createPositionOptionBySignProfile } from "../_util";
import type { ISDCElement } from "./ISDCElement";
import type { SDCPriceScale } from "../controller/SDCPriceScale";


export class SDCPricePositionSignGroup implements ISDCElement {
    private positionLines: SDCPricePosition[] = [];

    constructor(signProfile: ClassSignDetailInfo) {
        const params = createPositionOptionBySignProfile(signProfile, true);

        this.positionLines = params.map((param) => new SDCPricePosition(param));
    }

    public async show(widget: SDCWidget, intervalSeconds: number, priceScale: SDCPriceScale): Promise<void> {
        this.positionLines.forEach((positionLine) => positionLine.show(widget, intervalSeconds, priceScale));
    }

    public async hide(widget: SDCWidget): Promise<void> {
        this.positionLines.forEach((positionLine) => positionLine.hide(widget));
    }
}