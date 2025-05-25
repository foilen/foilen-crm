import axios from 'axios'
import Cookies from 'js-cookie'
import {toast} from 'react-toastify'

// Create an axios instance with default config
const http = axios.create({
    headers: {
        'Content-Type': 'application/json'
    }
})

// Add request interceptor to include CSRF token
http.interceptors.request.use(config => {
    const token = Cookies.get('XSRF-TOKEN')
    if (token) {
        config.headers['X-XSRF-TOKEN'] = token
    }
    return config
})

// Add response interceptor to handle errors
http.interceptors.response.use(
    response => {
        if (response.data && response.data.error) {
            showError(response.data.error)
            return Promise.reject(response.data.error)
        }
        return response
    },
    error => {
        let errorMessage = 'Error: '
        if (error.response) {
            errorMessage += `${error.response.status} ${error.response.statusText}`
            if (error.response.data) {
                errorMessage += ` - ${error.response.data}`
            }
        } else if (error.request) {
            errorMessage += 'No response received'
        } else {
            errorMessage += error.message
        }
        showError(errorMessage)
        return Promise.reject(error)
    }
)

// Show error message using react-toastify
export function showError(message) {
    if (typeof message !== 'string') {
        message = `${message.timestamp} ${message.uniqueId}: ${message.message}`
    }

    toast.error(message, {
        position: "top-right",
        autoClose: 5000,
        hideProgressBar: false,
        closeOnClick: true,
        pauseOnHover: true,
        draggable: true
    })

    console.error(message)
}

// Show success message using react-toastify
export function showSuccess(message) {
    toast.success(message, {
        position: "top-right",
        autoClose: 5000,
        hideProgressBar: false,
        closeOnClick: true,
        pauseOnHover: true,
        draggable: true
    })

    console.log('SUCCESS', message)
}

// HTTP GET request
export function get(url, params) {
    return http.get(url, {params})
}

// Function to ensure CSRF token is available
async function ensureCsrfToken() {
    if (!Cookies.get('XSRF-TOKEN')) {
        console.log('CSRF token not found, fetching it...')

        try {
            // Use axios directly with withCredentials to ensure cookies are sent/received
            await axios.get('/api/csrf', {withCredentials: true})
            console.log('CSRF token fetch request completed')

            // Wait a short time for the cookie to be set
            await new Promise(resolve => setTimeout(resolve, 100))

            // Verify that the token was set
            const token = Cookies.get('XSRF-TOKEN')
            if (token) {
                console.log('CSRF token is now available')
                return // Success, exit the function
            } else {
                console.warn('CSRF token still not available after fetch attempt')
            }
        } catch (error) {
            console.log('Error fetching CSRF token:', error)
        }

        console.error('Failed to fetch CSRF token')
    }
}

// HTTP POST request
export async function post(url, data) {
    await ensureCsrfToken()
    return http.post(url, data)
}

// HTTP PUT request
export async function put(url, data) {
    await ensureCsrfToken()
    return http.put(url, data)
}

// HTTP DELETE request
export async function del(url) {
    await ensureCsrfToken()
    return http.delete(url)
}

// Export del as delete for named imports
export {del as delete}

export default {
    get,
    post,
    put,
    delete: del,
    showError,
    showSuccess
}
