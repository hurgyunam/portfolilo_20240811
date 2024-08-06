import type { ClassSignDetailInfo } from "~/composables/sign-detail/service/_types";
import type { SDCScaler } from "./SDCScaler";
import type { SDCTimeScale } from "./SDCTimeScale";
import type { VisiblePriceRange, VisibleTimeRange } from "~/public/charting_library/charting_library";


const PADDING_PRICE_TOP_RATE = 15;
const PADDING_PRICE_BOTTOM_RATE = 10;
const PADDING_END_PERCENTAGE = 18;
const PADDING_ONGOING_RIGHT_MARGIN_PIXEL = 220;

export class SDCScaleController {
    private sdcScaler: SDCScaler;
    private sdcTimeScale: SDCTimeScale;
    private signProfile: ClassSignDetailInfo;
    private isEnd: boolean;

    constructor(sdcScaler: SDCScaler, sdcTimeScale: SDCTimeScale, signProfile: ClassSignDetailInfo, isEnd: boolean) {
        this.sdcScaler = sdcScaler;
        this.sdcTimeScale = sdcTimeScale;
        this.signProfile = signProfile;
        this.isEnd = isEnd;
    }

    public async getVisiblePriceRange(): Promise<VisiblePriceRange | null> {
        return this.sdcScaler.getVisiblePriceRange();
    }

    public async updateTimeScale(intervalSeconds: number): Promise<VisibleTimeRange> {
        const startSeconds = new Date(this.signProfile.createdAt).getUtcTsSeconds();

        if(this.isEnd && this.signProfile.endEarningTime) {
            const endSeconds = new Date(this.signProfile.endEarningTime).getUtcTsSeconds();

            const visibleTimeRange = await this.sdcTimeScale.createTimeRateRange(startSeconds, endSeconds, PADDING_END_PERCENTAGE, PADDING_END_PERCENTAGE, intervalSeconds);
    
            await this.sdcScaler.setVisibleTimeRange(visibleTimeRange);

            return visibleTimeRange;
        } else {
            const maxTs = await this.sdcScaler.getMaxTs();
            const limitSeconds = startSeconds + 38 * intervalSeconds;

            if(maxTs > limitSeconds) {
                // console.log("hello", new Date(maxTs*1000).format("yyyy/MM/dd HH:mm:ss"), new Date(this.signProfile.createdAt).format("yyyy/MM/dd HH:mm:ss"));
                const minTs = new Date(this.signProfile.createdAt).getUtcTsSeconds() - intervalSeconds * 20;

                // console.log("hello minTs", new Date(minTs*1000).format("yyyy/MM/dd HH:mm:ss"), intervalSeconds);
                const visibleTimeRange = {
                    from: minTs,
                    to: maxTs,
                }

                await this.sdcScaler.setVisibleTimeRange(visibleTimeRange);
    
                return visibleTimeRange;
            } else {
                const visibleTimeRange = this.sdcTimeScale.createTimeMarginRightPixel(startSeconds, PADDING_ONGOING_RIGHT_MARGIN_PIXEL, intervalSeconds);
    
                await this.sdcScaler.setVisibleTimeRange(visibleTimeRange);
    
                return visibleTimeRange;
            }
        }
    }   
    
    public async updatePriceScale(): Promise<VisiblePriceRange | null> {
        // const lastCandle = await this.sdcScaler.getLastCandle();
        const candleMinPrice = await this.sdcScaler.getMinPrice();
        const candleMaxPrice = await this.sdcScaler.getMaxPrice();

        const minPrice = Math.min(this.signProfile.startPrice, this.signProfile.targetPrice3, candleMinPrice);
        const maxPrice = Math.max(this.signProfile.startPrice, this.signProfile.targetPrice3, candleMaxPrice);

        const paddingPrice = (maxPrice - minPrice) / (100 - PADDING_PRICE_TOP_RATE - PADDING_PRICE_BOTTOM_RATE); // 10 * 1%

        const visiblePriceRange: VisiblePriceRange = {
            from: minPrice - PADDING_PRICE_BOTTOM_RATE * paddingPrice,
            to: maxPrice + PADDING_PRICE_TOP_RATE * paddingPrice,
        };
        
        const result = await this.sdcScaler.setVisiblePriceRange(visiblePriceRange);

        if(result) {
            return visiblePriceRange;
        } else {
            return null;
        }
    }
}