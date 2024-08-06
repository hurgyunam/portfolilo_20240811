
export enum EInterval {
    M15 = '15',
    H1 = '1H',
    H4 = '4H',
    D1 = '1D',
    D7 = '7D',
}

export type TInterval = EInterval.M15 | EInterval.H1 | EInterval.H4 | EInterval.D1 | EInterval.D7;
