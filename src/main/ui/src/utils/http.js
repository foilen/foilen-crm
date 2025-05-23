import axios from 'axios'
import Cookies from 'js-cookie'

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
        errorMessage += `<br/>${error.response.data}`
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

// Show error message
export function showError(message) {
  if (typeof message !== 'string') {
    message = `${message.timestamp} ${message.uniqueId}: ${message.message}`
  }

  const errors = document.getElementById('errors')
  if (errors) {
    const error = document.createElement('div')
    error.innerHTML = `${message}<button type="button" class="close" data-dismiss="alert">&times;</button>`
    error.setAttribute('class', 'alert alert-danger alert-dismissible fade show')
    errors.appendChild(error)
  }

  console.error(message)
}

// Show success message
export function showSuccess(message) {
  const successes = document.getElementById('successes')
  if (successes) {
    const success = document.createElement('div')
    success.innerHTML = `${message}<button type="button" class="close" data-dismiss="alert">&times;</button>`
    success.setAttribute('class', 'alert alert-success alert-dismissible fade show')
    successes.appendChild(success)

    // Auto-hide after 20 seconds
    setTimeout(() => {
      success.style.display = 'none'
    }, 20000)
  }

  console.log('SUCCESS', message)
}

// HTTP GET request
export function get(url, params) {
  return http.get(url, { params })
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

export default {
  get,
  post,
  put,
  delete: del,
  showError,
  showSuccess
}