import Vue from 'vue'
import VueRouter from 'vue-router'
import VueI18n from 'vue-i18n'
import App from './App.vue'
import router from './router'
import 'bootstrap/dist/css/bootstrap.min.css'
import 'bootstrap'
import $ from 'jquery'
import Cookies from 'js-cookie'

// Make jQuery available globally
window.jQuery = window.$ = $
window.Cookies = Cookies

// Use Vue Router and i18n
Vue.use(VueRouter)
Vue.use(VueI18n)

// Create i18n instance
const i18n = new VueI18n({
  locale: 'en',
  messages: {}
})

// Disable production tip
Vue.config.productionTip = false

// Create the Vue instance when the DOM is ready
document.addEventListener('DOMContentLoaded', () => {
  new Vue({
    router,
    i18n,
    render: h => h(App)
  }).$mount('#app')
})
