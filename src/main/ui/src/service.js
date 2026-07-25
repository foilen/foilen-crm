import axios from 'axios'
import Cookies from 'js-cookie'
import {toast} from 'react-toastify'

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

class Service {
    constructor() {
        this.axiosInstance = axios.create({
            headers: {
                'Content-Type': 'application/json'
            }
        })

        // Add request interceptor to include CSRF token
        this.axiosInstance.interceptors.request.use(config => {
            const token = Cookies.get('XSRF-TOKEN')
            if (token) {
                config.headers['X-XSRF-TOKEN'] = token
            }
            return config
        })

        // Add response interceptor to handle errors
        this.axiosInstance.interceptors.response.use(
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
    }

    // Ensure CSRF token is available
    async ensureCsrfToken() {
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

    get(url, params) {
        return this.axiosInstance.get(url, {params})
    }

    async post(url, data) {
        await this.ensureCsrfToken()
        return this.axiosInstance.post(url, data)
    }

    async put(url, data) {
        await this.ensureCsrfToken()
        return this.axiosInstance.put(url, data)
    }

    async delete(url) {
        await this.ensureCsrfToken()
        return this.axiosInstance.delete(url)
    }

    // Admin

    adminExport() {
        return this.get('/api/admin/export')
    }

    adminImport(exportModel) {
        return this.post('/api/admin/import', exportModel)
    }

    // App

    appDetails() {
        return this.get('/api/app/details')
    }

    // Client

    clientCreate(form) {
        return this.post('/api/client', form)
    }

    clientDelete(clientShortName) {
        return this.delete(`/api/client/${clientShortName}`)
    }

    clientListAll(pageId, search) {
        return this.get('/api/client/listAll', {pageId, search})
    }

    clientUpdate(clientShortName, form) {
        return this.put(`/api/client/${clientShortName}`, form)
    }

    // Item

    itemBillPending(form) {
        return this.post('/api/item/billPending', form)
    }

    itemBillSomePending(form) {
        return this.post('/api/item/billSomePending', form)
    }

    itemCreate(form) {
        return this.post('/api/item', form)
    }

    itemCreateWithTime(form) {
        return this.post('/api/item/createWithTime', form)
    }

    itemDelete(id) {
        return this.delete(`/api/item/${id}`)
    }

    itemListBilled(pageId) {
        return this.get('/api/item/listBilled', {pageId})
    }

    itemListCategories() {
        return this.get('/api/item/listCategories')
    }

    itemListPending(pageId) {
        return this.get('/api/item/listPending', {pageId})
    }

    itemUpdate(id, form) {
        return this.put(`/api/item/${id}`, form)
    }

    // RecurrentItem

    recurrentItemCreate(form) {
        return this.post('/api/recurrentItem', form)
    }

    recurrentItemDelete(id) {
        return this.delete(`/api/recurrentItem/${id}`)
    }

    recurrentItemListAll(pageId) {
        return this.get('/api/recurrentItem/listAll', {pageId})
    }

    recurrentItemUpdate(id, form) {
        return this.put(`/api/recurrentItem/${id}`, form)
    }

    // Report

    reportGet() {
        return this.get('/api/report')
    }

    // TechnicalSupport

    technicalSupportCreate(form) {
        return this.post('/api/technicalSupport', form)
    }

    technicalSupportDelete(sid) {
        return this.delete(`/api/technicalSupport/${sid}`)
    }

    technicalSupportListAll(pageId, search) {
        return this.get('/api/technicalSupport/listAll', {pageId, search})
    }

    technicalSupportUpdate(sid, form) {
        return this.put(`/api/technicalSupport/${sid}`, form)
    }

    // Transaction

    transactionCreatePayment(form) {
        return this.post('/api/transaction/payment', form)
    }

    transactionListAll(pageId) {
        return this.get('/api/transaction/listAll', {pageId})
    }

    transactionUpdate(id, form) {
        return this.put(`/api/transaction/${id}`, form)
    }

    // User

    userListAll(pageId, search) {
        return this.get('/api/user/listAll', {pageId, search})
    }

    userUpdateAdmin(id, form) {
        return this.put(`/api/user/${id}/admin`, form)
    }
}

const service = new Service()

export default service
