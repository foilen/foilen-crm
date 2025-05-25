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

// HTTP POST request
export function post(url, data) {
    return http.post(url, data)
}

// HTTP PUT request
export function put(url, data) {
    return http.put(url, data)
}

// HTTP DELETE request
export function del(url) {
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
