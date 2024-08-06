import type { CDCWidget } from "../controller/CDCWidget";

export interface ICDCElement {
    show(ctvChart: CDCWidget): void;
    hide(ctvChart: CDCWidget): void;
}