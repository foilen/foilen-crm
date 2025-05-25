import React from 'react'

// Global variable to store the application details
let appDetails = {
    lang: 'en',
    translations: {en: {}, fr: {}}
}

// Function to update the application details
export const updateAppDetails = (newAppDetails) => {
    appDetails = newAppDetails
}

/**
 * Global translation function
 * @param {string} key - The translation key (e.g., 'home.welcome')
 * @param {object} options - Optional parameters for interpolation
 * @returns {string} The translated string
 */
export const t = (key, options = {}) => {
    // Get the current language
    const lang = appDetails.lang || 'en'

    // Get the translations for the current language
    const translations = appDetails.translations[lang] || {}

    // Get the translation for the key
    let translation = translations[key]

    // If translation not found, return the key
    if (!translation) {
        console.warn(`Translation not found for key: ${key}`)
        return key
    }

    // Handle interpolation
    if (options) {
        Object.keys(options).forEach(optionKey => {
            const value = options[optionKey]

            // If the value is a React element, replace the placeholder with a special marker
            // that we can use to split the string and insert the element
            if (React.isValidElement(value)) {
                const placeholder = `{{${optionKey}}}`
                const marker = `__REACT_ELEMENT_${optionKey}__`
                translation = translation.replace(placeholder, marker)
            } else {
                // For regular values, just replace the placeholder
                const placeholder = `{{${optionKey}}}`
                translation = translation.replace(placeholder, value)
            }
        })

        // If there are React elements, split the string and create an array of strings and elements
        const hasReactElements = Object.keys(options).some(key => React.isValidElement(options[key]))
        if (hasReactElements) {
            const parts = []
            let lastIndex = 0

            // Find all markers and split the string
            Object.keys(options).forEach(optionKey => {
                if (React.isValidElement(options[optionKey])) {
                    const marker = `__REACT_ELEMENT_${optionKey}__`
                    const index = translation.indexOf(marker, lastIndex)

                    if (index !== -1) {
                        // Add the text before the marker
                        if (index > lastIndex) {
                            parts.push(translation.substring(lastIndex, index))
                        }

                        // Add the React element
                        parts.push(options[optionKey])

                        // Update the last index
                        lastIndex = index + marker.length
                    }
                }
            })

            // Add any remaining text
            if (lastIndex < translation.length) {
                parts.push(translation.substring(lastIndex))
            }

            // If there's only one part, return it directly
            if (parts.length === 1) {
                return parts[0]
            }

            // Otherwise, return an array (React will render it correctly)
            return parts
        }
    }

    return translation
}

// Export a default object for compatibility with existing code
export default {t, updateAppDetails}
