import type { AvailableSaveloadVersions, ChartPropertiesOverrides, ChartingLibraryFeatureset, ChartingLibraryWidgetOptions, ResolutionString, Timezone, TradingTerminalFeatureset, WidgetOverrides } from "~/public/charting_library/charting_library";
import { getLanguageFromURL } from "./methods";


export const defaultOption: Partial<ChartingLibraryWidgetOptions> = {
    library_path: '/charting_library/',
    charts_storage_url: 'https://saveload.tradingview.com',
    charts_storage_api_version: '1.1' as AvailableSaveloadVersions,
    client_id: 'tradingview.com',
    user_id: 'public_user_id',
    studies_overrides: {},
}

export const loadOptions: Partial<ChartingLibraryWidgetOptions> = {
    fullscreen: false,
    autosize: true,
    locale: getLanguageFromURL() || 'en',
    loading_screen: {
        'backgroundColor': '#F4F6F9',
        'foregroundColor': '#F4F6F9'
    },
    timezone: Intl.DateTimeFormat().resolvedOptions().timeZone as Timezone,
}

export const features_options:{
    disabled_features?: ChartingLibraryFeatureset[] | TradingTerminalFeatureset[];
    enabled_features?: ChartingLibraryFeatureset[];
} = {
    disabled_features: ['use_localstorage_for_settings', 'header_widget', 'left_toolbar', 'timeframes_toolbar', 'right_toolbar', 'legend_widget', 'legend_context_menu'],
    enabled_features: ['study_templates'],
}


export const overrides: Partial<WidgetOverrides> = {
    "linetoolhorzline.linecolor": 'rgba(48, 164, 22, 0.8)',
}