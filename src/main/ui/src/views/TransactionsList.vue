<template>
  <div class="row">
    <div class="col-12">
      <button type="button" class="btn btn-success" data-bs-toggle="modal" data-bs-target="#createModal">
        {{ $t('button.createPayment') }}
      </button>

      <div class="modal fade" id="createModal" tabindex="-1" role="dialog" aria-labelledby="createModalLabel"
           aria-hidden="true">
        <div class="modal-dialog" role="document">
          <div class="modal-content">
            <div class="modal-header">
              <h5 class="modal-title" id="createModalLabel">{{ $t('button.create') }}</h5>
              <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
            </div>
            <div class="modal-body">
              <error-results :formResult="formResult"></error-results>

              <div class="mb-3">
                <label for="clientShortName">{{ $t('term.clientShortName') }}</label>
                <client-select id="clientShortName" v-model="form.clientShortName"></client-select>
                <div class="text-danger"
                     v-if="formResult.validationErrorsByField && formResult.validationErrorsByField.clientShortName">
                  <p v-for="errorCode in formResult.validationErrorsByField.clientShortName" :key="errorCode">
                    {{ $t(errorCode) }}</p>
                </div>
              </div>

              <div class="mb-3">
                <label for="date">{{ $t('term.date') }}</label> <input type="text" class="form-control" id="date"
                                                                       v-model="form.date" autocomplete="off">
                <div class="text-danger"
                     v-if="formResult.validationErrorsByField && formResult.validationErrorsByField.date">
                  <p v-for="errorCode in formResult.validationErrorsByField.date" :key="errorCode">{{
                      $t(errorCode)
                    }}</p>
                </div>
              </div>

              <div class="mb-3">
                <label for="paymentType">{{ $t('term.paymentType') }}</label> <input type="text" class="form-control"
                                                                                     id="paymentType"
                                                                                     v-model="form.paymentType"
                                                                                     autocomplete="off">
                <div class="text-danger"
                     v-if="formResult.validationErrorsByField && formResult.validationErrorsByField.paymentType">
                  <p v-for="errorCode in formResult.validationErrorsByField.paymentType" :key="errorCode">
                    {{ $t(errorCode) }}</p>
                </div>
              </div>

              <div class="mb-3">
                <label for="price">{{ $t('term.price') }}</label> <input type="text" class="form-control" id="price"
                                                                         v-model="form.price" autocomplete="off">
                <div class="text-danger"
                     v-if="formResult.validationErrorsByField && formResult.validationErrorsByField.price">
                  <p v-for="errorCode in formResult.validationErrorsByField.price" :key="errorCode">{{
                      $t(errorCode)
                    }}</p>
                </div>
              </div>
            </div>
            <div class="modal-footer">
              <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">{{ $t('button.close') }}</button>
              <button type="button" class="btn btn-success" @click="create()">{{ $t('button.create') }}</button>
            </div>
          </div>
        </div>
      </div>
    </div>

    <div class="col-12">
      <pagination class="float-end" :pagination="pagination" @changePage="refresh($event.pageId)"></pagination>

      <table class="table table-striped">
        <thead>
        <tr>
          <th scope="col">{{ $t('term.invoiceId') }}</th>
          <th scope="col">{{ $t('term.client') }}</th>
          <th scope="col">{{ $t('term.date') }}</th>
          <th scope="col">{{ $t('term.description') }}</th>
          <th scope="col">{{ $t('term.price') }}</th>
        </tr>
        </thead>
        <tbody>
        <tr v-for="item in items" :key="item.id">
          <td>{{ item.invoiceId }}</td>
          <td>{{ item.client.name }} ({{ item.client.email }}) ({{ item.client.lang }})</td>
          <td>{{ item.dateFormatted }}</td>
          <td>{{ item.description }}</td>
          <td>{{ item.priceFormatted }}</td>
        </tr>
        </tbody>
      </table>
    </div>
  </div>
</template>

<script>
import ErrorResults from '@/components/ErrorResults.vue'
import Pagination from '@/components/Pagination.vue'
import ClientSelect from '@/components/ClientSelect.vue'
import http from '@/utils/http'
import {dateNowDayOnly, priceToLong} from '@/utils/features'

export default {
  name: 'TransactionsList',
  components: {
    ErrorResults,
    Pagination,
    ClientSelect
  },
  data() {
    return {
      queries: {},
      items: [],
      pagination: {
        currentPageUi: 1,
        totalPages: 1,
        firstPage: true,
        lastPage: true,
      },
      form: {
        date: dateNowDayOnly(),
      },
      formResult: {},
    }
  },
  methods: {
    create() {
      this.formResult = {}
      const clonedForm = JSON.parse(JSON.stringify(this.form))
      clonedForm.price = priceToLong(clonedForm.price)
      console.log('Transaction Payment - Create', clonedForm)

      http.post('/api/transaction/payment', clonedForm)
          .then(response => {
            this.formResult = response.data
            if (response.data.success) {
              document.querySelector('#createModal .btn-secondary').click()
              this.refresh(this.queries.pageId)
            }
          })
          .catch(error => {
            console.error('Error creating payment', error)
          })
    },
    refresh(pageId) {
      if (pageId === undefined) {
        pageId = 1
      }
      this.queries.pageId = pageId
      console.log('Transactions - Load', this.queries)

      http.get('/api/transaction/listAll', this.queries)
          .then(response => {
            this.pagination = response.data.pagination
            this.items = response.data.items || []
          })
          .catch(error => {
            console.error('Error loading transactions', error)
            this.items = []
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
