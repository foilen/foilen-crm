import Vue from 'vue'
import VueRouter from 'vue-router'
import Home from './views/Home.vue'
import ClientsList from './views/ClientsList.vue'
import ItemsList from './views/ItemsList.vue'
import RecurrentItemsList from './views/RecurrentItemsList.vue'
import ReportsList from './views/ReportsList.vue'
import TechnicalSupportsList from './views/TechnicalSupportsList.vue'
import TransactionsList from './views/TransactionsList.vue'

Vue.use(VueRouter)

const routes = [
  {
    path: '/',
    component: Home
  },
  {
    path: '/clients',
    component: ClientsList
  },
  {
    path: '/items',
    component: ItemsList
  },
  {
    path: '/recurrentItems',
    component: RecurrentItemsList
  },
  {
    path: '/reports',
    component: ReportsList
  },
  {
    path: '/technicalSupports',
    component: TechnicalSupportsList
  },
  {
    path: '/transactions',
    component: TransactionsList
  }
]

const router = new VueRouter({
  routes
})

export default router