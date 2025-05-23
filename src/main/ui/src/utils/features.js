/**
 * Utility functions for the application
 */

/**
 * Returns the current date in the format YYYY-MM-DD
 * @returns {string} The current date in the format YYYY-MM-DD
 */
export function dateNowDayOnly() {
  const d = new Date()
  return d.getFullYear() + '-' + ("0" + (d.getMonth() + 1)).slice(-2) + '-' + ("0" + d.getDate()).slice(-2)
}

/**
 * Sets all properties of an object to null
 * @param {Object} object The object to modify
 */
export function nullAllProperties(object) {
  Object.keys(object).forEach(k => object[k] = null)
}

/**
 * Converts a price string to a long integer (cents)
 * @param {string} price The price string to convert
 * @returns {number} The price in cents
 */
export function priceToLong(price) {
  return Math.round(parseFloat(price) * 100)
}

/**
 * Initializes typeahead dropdowns
 * This is a no-op in the new project structure as we're using Vue's built-in functionality
 */
export function refreshTypeahead() {
  // No-op - Vue handles this functionality
}

export default {
  dateNowDayOnly,
  nullAllProperties,
  priceToLong,
  refreshTypeahead
}