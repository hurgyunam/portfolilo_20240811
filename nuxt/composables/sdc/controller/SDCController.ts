import type { EInterval, TInterval } from "~/composables/_tv-tech-chart/_types";
import { SDCWidget } from "./SDCWidget";
import type { IPositionLineAdapter, VisiblePriceRange, VisibleTimeRange } from "~/public/charting_library/charting_library";
import type { IPositionParam } from "../_types";
import { SDCTimeScale } from "./SDCTimeScale";
import type { ClassSignDetailInfo } from "../../../service/_types";
import type { ISDCElement } from "../elements/ISDCElement";
import { SDCPriceScale } from "./SDCPriceScale";


const PADDING_END_PERCENTAGE = 18;
const PADDING_ONGOING_RIGHT_MARGIN_PIXEL = 220;

export class SDCController {
    private sdcWidget: SDCWidget;
    private visiblePriceRange: VisiblePriceRange | null = null;
    private visibleTimeRange: VisibleTimeRange | null = null;

    constructor(cryptoId: number, interval: TInterval, container: HTMLElement, datafeedUrl: string) {
        this.sdcWidget = new SDCWidget(cryptoId, interval, container, datafeedUrl);
    }

    public async updateScale (signProfile: ClassSignDetailInfo, intervalSeconds: number): Promise<VisibleTimeRange | null> {
        const isEnd = ["sell_fail", "sell_success", "sell_success2", "sell_success3", "buy_fail", "cut"].includes(signProfile.evaluateState);    
        const scaler = await this.sdcWidget.getScaler(signProfile, isEnd);

        const visiblePriceRange = await scaler.updatePriceScale();

        if(visiblePriceRange) {
            this.visiblePriceRange = visiblePriceRange;
        }

        const visibleTimeRange = await scaler.updateTimeScale(intervalSeconds);;

        if(visibleTimeRange) {
            this.visibleTimeRange = visibleTimeRange;
        }

        return visibleTimeRange;
    }

    public async show (element: ISDCElement, intervalSeconds: number): Promise<void>{
        const heightPixel = await this.sdcWidget.getHeightPixel();

        if(heightPixel !== null && this.visiblePriceRange !== null) {
            await element.show(
                this.sdcWidget, 
                intervalSeconds, 
                new SDCPriceScale(
                    heightPixel,
                    this.visiblePriceRange,
                )
            );
        }

    }

    public async hide (element: ISDCElement): Promise<void> {
        await element.hide(this.sdcWidget);
    }

    public async setInterval(interval: EInterval): Promise<void> {
        await this.sdcWidget.setInterval(interval);
    }

    public async getVisiblePriceRange(): Promise<VisiblePriceRange | null> {
        return this.visibleTimeRange;
    }



}