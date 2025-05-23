<template>
  <div class="row">
    <div class="col-12">
      <h1>{{ $t('term.balanceByClient') }}</h1>

      <table class="table table-striped">
        <thead>
          <tr>
            <th scope="col">{{ $t('term.client') }}</th>
            <th scope="col">{{ $t('term.total') }}</th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="item in reports.balanceByClient" :key="item.clientName">
            <td>{{item.clientName}}</td>
            <td>{{item.totalFormatted}}$</td>
          </tr>
        </tbody>
      </table>

      <p>{{ $t('term.globalBalance') }}: {{reports.globalBalanceFormatted}}$</p>

      <h1>{{ $t('term.itemsByCategory') }}</h1>

      <table class="table table-striped">
        <thead>
          <tr>
            <th scope="col">{{ $t('term.date') }}</th>
            <th scope="col">{{ $t('term.category') }}</th>
            <th scope="col">{{ $t('term.total') }}</th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="item in reports.itemsByCategory" :key="item.monthDate + '-' + item.category">
            <td>{{item.monthDate}}</td>
            <td>{{item.category}}</td>
            <td>{{item.totalFormatted}}$</td>
          </tr>
        </tbody>
      </table>
    </div>
  </div>
</template>

<script>
import http from '@/utils/http'

export default {
  name: 'ReportsList',
  data() {
    return {
      reports: {}
    }
  },
  methods: {
    refresh() {
      console.log('Reports - Load')
      http.get('/api/report')
        .then(response => {
          this.reports = response.data.item || {}
        })
        .catch(error => {
          console.error('Error loading reports', error)
          this.reports = {}
        })
    }
  },
  mounted() {
    this.refresh()
  }
}
</script>

<style scoped>
</style>