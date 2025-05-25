export function toLangOnly(lang) {
    // Remove everything after the '-' character
    return lang.split('-')[0]
}
