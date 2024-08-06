import { headAndShoulders, penants, triangles, xabcds, type IPatternButton, type MultipointShape, type PatternType, type Point } from "~/composables/coin/tv-tech-chart/services/_types";
import type { CreateMultipointShapeOptions, EntityId } from "~/public/charting_library/charting_library";
import type { ICDCElement } from "./ICDCElement";
import type { CDCWidget } from "../controller/CDCWidget";

interface IDrawShapePoint {
    points: Point[];
    shape: "triangle_pattern" | "xabcd_pattern";
}

export class CDCPattern implements ICDCElement {
    private entityIds: EntityId[] = [];

    private patternScanId: number;
    private startSeconds: number;
    private endSeconds: number;
    private patternName: string;

    private shapes: MultipointShape[];
    // private auxOptions: CreateMultipointShapeOptions<object> | null;
    // private auxPoints: Point[];

    constructor(patternScanId: number, patternType: PatternType, patternName: string, points: Point[], startSeconds: number, endSeconds: number) {
        this.patternScanId = patternScanId;
        this.patternName = patternName;

        const isHeadAndShoulder = headAndShoulders.includes(patternType);
        this.startSeconds = startSeconds;
        this.endSeconds = endSeconds;
        const options: CreateMultipointShapeOptions<object> = {
            shape: isHeadAndShoulder? 'head_and_shoulders' : 'trend_line',
            // shape: 'rectangle',
            lock: true,
            disableSelection: true,
            disableSave: true,
            disableUndo: true,
            text: "text",
            overrides: {
                'linewidth': 3,
                'linecolor': 'green',
            }
        }

        if(!isHeadAndShoulder) {
            this.shapes = [
                ...Array.from(Array(points.length - 1).keys()).map((index) => {
                    return points.slice(index, index+2)
                }).map((points) => {
                    return {
                        points,
                        options
                    }
                }),
                {
                    points: penants.includes(patternType) && points.length > 4? points.slice(1, 5) :
                            triangles.includes(patternType) && points.length > 3? points.slice(0, 4) :
                            xabcds.includes(patternType) && points.length > 4? points.slice(0, 5) :
                            [],
                    options: {
                        shape: xabcds.includes(patternType)? "xabcd_pattern": "triangle_pattern",
                        lock: true,
                    }
                }
            ]
        } else {
            this.shapes = [
                {
                    points,
                    options
                }
            ]
        }

        // console.log("hello", patternType, headAndShoulders.includes(patternType), penants.includes(patternType), triangles.includes(patternType), xabcds.includes(patternType));
    }

    public async show(cdcWidget: CDCWidget): Promise<void> {
        this.entityIds = await cdcWidget.createMultipointShapes(this.shapes);
    }

    public hide(ctvChart: CDCWidget): void {
        ctvChart.removeShapes(this.entityIds);
        this.entityIds = [];
    }

    public getPatternScanId(): number {
        return this.patternScanId;
    }

    public getStartSeconds(): number {
        return this.startSeconds;
    }

    public getEndSeconds(): number {
        return this.endSeconds;
    }

    public getPatternButton(): IPatternButton {
        return {
            patternScanId: this.patternScanId,
            patternName: this.patternName,
        }
    }

}