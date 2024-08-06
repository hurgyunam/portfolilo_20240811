import type { ClassSignDetailInfo } from "~/composables/sign-detail/service/_types";
import type { IPositionParam } from "./_types";
import { COLOR_BLACK, COLOR_CHART_BG, COLOR_CUTPRICE, COLOR_GRAY, COLOR_LONG, COLOR_LONG_1, COLOR_LONG_2, COLOR_LONG_3, COLOR_SHORT, COLOR_SHORT_1, COLOR_SHORT_2, COLOR_SHORT_3, COLOR_WHITE } from "~/composables/_tv-tech-chart/common";


export const createPositionOptionBySignProfile = (signProfile: ClassSignDetailInfo, isVisibleEarningRate: boolean): IPositionParam[] => {
    const isUp = signProfile.startPrice < signProfile.targetPrice;

    if(isVisibleEarningRate) {
        return [
            {
                price: signProfile.cutPrice,
                label: '손절가',
                lineColor: COLOR_CUTPRICE,
                labelBgColor: COLOR_CUTPRICE,
                labelTextColor: COLOR_BLACK,
                // borderColor: COLOR_GRAY,
                // marginLeft: 100,
                marginRightPixel: 0,
            },
            {
                price: signProfile.startPrice,
                label: '진입가',
                lineColor: COLOR_BLACK,
                labelBgColor: COLOR_BLACK,
                labelTextColor: COLOR_WHITE,
                // marginLeft: 40,
                marginRightPixel: 130,
            },
            {
                price: signProfile.targetPrice,
                label: '목표 1',
                lineColor: isUp? COLOR_LONG_1 : COLOR_SHORT_1,
                labelBgColor: isUp? COLOR_LONG_1 : COLOR_SHORT_1,
                labelTextColor: COLOR_WHITE,
                // marginLeft: 50,
                rate: signProfile.targetEarningRate,
                rateBgColor: COLOR_WHITE,
                rateTextColor: COLOR_LONG,
                marginRightPixel: 55,
            },
            {
                price: signProfile.targetPrice2,
                label: '목표 2',
                lineColor: isUp? COLOR_LONG_2 : COLOR_SHORT_2,
                labelBgColor: isUp? COLOR_LONG_2 : COLOR_SHORT_2,
                labelTextColor: COLOR_WHITE,
                // marginLeft: 60,
                rate: signProfile.targetEarningRate2,
                rateBgColor: COLOR_WHITE,
                rateTextColor: COLOR_LONG,
                marginRightPixel: 35,
            },
            {
                price: signProfile.targetPrice3,
                label: '목표 3',
                lineColor: isUp? COLOR_LONG_3 : COLOR_SHORT_3,
                labelBgColor: isUp? COLOR_LONG_3 : COLOR_SHORT_3,
                labelTextColor: COLOR_WHITE,
                // marginLeft: 70,
                rate: signProfile.targetEarningRate3,
                rateBgColor: COLOR_WHITE,
                rateTextColor: COLOR_LONG,
                marginRightPixel: 20,
            },
        ];
    } else {
        return [
            {
                price: signProfile.cutPrice,
                label: '손절가',
                lineColor: COLOR_CUTPRICE,
                labelBgColor: COLOR_CUTPRICE,
                labelTextColor: COLOR_BLACK,
                borderColor: COLOR_GRAY,
                marginLeft: 60,
                marginStartTimeRate: 0,
            },
            {
                price: signProfile.startPrice,
                lineColor: COLOR_BLACK,
                labelBgColor: COLOR_BLACK,
                labelTextColor: COLOR_WHITE,
                marginLeft: 60,
                marginStartTimeRate: 0,
            },
            {
                price: signProfile.targetPrice,
                lineColor: isUp? COLOR_LONG_1 : COLOR_SHORT_1,
                marginLeft: 60,
                marginStartTimeRate: 0,
            },
            {
                price: signProfile.targetPrice2,
                lineColor: isUp? COLOR_LONG_2 : COLOR_SHORT_2,
                marginLeft: 60,
                marginStartTimeRate: 0,
            },
            {
                price: signProfile.targetPrice3,
                lineColor: isUp? COLOR_LONG_3 : COLOR_SHORT_3,
                marginLeft: 60,
                marginStartTimeRate: 0,
            },
        ];
    }
}

export const getTextByteCount = (text: string) => {
    return text.split("").map((char) => {
        // console.log("aaa", char);
        return char.match(/[a-zA-Z0-9 %]/g)? 1 : 2;
    }).reduce((prev, cur) => prev + cur, 0);
}