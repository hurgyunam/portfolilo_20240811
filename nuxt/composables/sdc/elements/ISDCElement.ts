import type { VisiblePriceRange } from "~/public/charting_library/charting_library";
import type { SDCWidget } from "../controller/SDCWidget";
import type { SDCPriceScale } from "../controller/SDCPriceScale";


export interface ISDCElement {
    show: (widget: SDCWidget, intervalSeconds: number, priceScale: SDCPriceScale) => Promise<void>;
    hide: (widget: SDCWidget) => Promise<void>;
}