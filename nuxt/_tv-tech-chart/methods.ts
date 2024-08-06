import type { LanguageCode } from "~/public/charting_library/charting_library";

export const getLanguageFromURL = (): LanguageCode | null => {
    if (!window) return null;

	const regex = new RegExp('[\\?&]lang=([^&#]*)');
	const results = regex.exec(window.location.search);
	return results === null ? null : decodeURIComponent(results[1].replace(/\+/g, ' ')) as LanguageCode;
};