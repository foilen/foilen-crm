<template>
  <div class="row">
    <div class="col-12">
      <h1>{{ $t('term.pending') }}</h1>

      <button type="button" class="btn btn-success" data-bs-toggle="modal" @click="showCreate()" data-bs-target="#createModal">{{ $t('button.create') }}</button>

      <div class="modal fade" id="createModal" tabindex="-1" role="dialog" aria-labelledby="createModalLabel" aria-hidden="true">
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
                <client-select id="clientShortName" v-model="createForm.clientShortName"></client-select>
                <div class="text-danger" v-if="formResult.validationErrorsByField && formResult.validationErrorsByField.clientShortName">
                  <p v-for="errorCode in formResult.validationErrorsByField.clientShortName" :key="errorCode">{{ $t(errorCode) }}</p>
                </div>
              </div>

              <div class="mb-3">
                <label for="date">{{ $t('term.date') }}</label> <input type="text" class="form-control" id="date" v-model="createForm.date" autocomplete="off">
                <div class="text-danger" v-if="formResult.validationErrorsByField && formResult.validationErrorsByField.date">
                  <p v-for="errorCode in formResult.validationErrorsByField.date" :key="errorCode">{{ $t(errorCode) }}</p>
                </div>
              </div>

              <div class="mb-3">
                <label for="description">{{ $t('term.description') }}</label> <input type="text" class="form-control" id="description" v-model="createForm.description" autocomplete="off">
                <div class="text-danger" v-if="formResult.validationErrorsByField && formResult.validationErrorsByField.description">
                  <p v-for="errorCode in formResult.validationErrorsByField.description" :key="errorCode">{{ $t(errorCode) }}</p>
                </div>
              </div>

              <div class="mb-3">
                <label for="price">{{ $t('term.price') }}</label> <input type="text" class="form-control" id="price" v-model="createForm.price" autocomplete="off">
                <div class="text-danger" v-if="formResult.validationErrorsByField && formResult.validationErrorsByField.price">
                  <p v-for="errorCode in formResult.validationErrorsByField.price" :key="errorCode">{{ $t(errorCode) }}</p>
                </div>
              </div>

              <div class="mb-3">
                <label for="category">{{ $t('term.category') }}</label> <input type="text" class="form-control" id="category" v-model="createForm.category" autocomplete="off">
                <div class="text-danger" v-if="formResult.validationErrorsByField && formResult.validationErrorsByField.category">
                  <p v-for="errorCode in formResult.validationErrorsByField.category" :key="errorCode">{{ $t(errorCode) }}</p>
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

      <div class="modal fade" id="editModal" tabindex="-1" role="dialog" aria-labelledby="editModalLabel" aria-hidden="true">
        <div class="modal-dialog" role="document">
          <div class="modal-content">
            <div class="modal-header">
              <h5 class="modal-title" id="editModalLabel">{{ $t('button.edit') }}</h5>
              <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
            </div>
            <div class="modal-body">
              <error-results :formResult="formResult"></error-results>

              <div class="mb-3">
                <label for="clientShortName2">{{ $t('term.clientShortName') }}</label>
                <client-select id="clientShortName2" v-model="editForm.clientShortName"></client-select>
                <div class="text-danger" v-if="formResult.validationErrorsByField && formResult.validationErrorsByField.clientShortName">
                  <p v-for="errorCode in formResult.validationErrorsByField.clientShortName" :key="errorCode">{{ $t(errorCode) }}</p>
                </div>
              </div>

              <div class="mb-3">
                <label for="date2">{{ $t('term.date') }}</label> <input type="text" class="form-control" id="date2" v-model="editForm.date" autocomplete="off">
                <div class="text-danger" v-if="formResult.validationErrorsByField && formResult.validationErrorsByField.date">
                  <p v-for="errorCode in formResult.validationErrorsByField.date" :key="errorCode">{{ $t(errorCode) }}</p>
                </div>
              </div>

              <div class="mb-3">
                <label for="description2">{{ $t('term.description') }}</label> <input type="text" class="form-control" id="description2" v-model="editForm.description" autocomplete="off">
                <div class="text-danger" v-if="formResult.validationErrorsByField && formResult.validationErrorsByField.description">
                  <p v-for="errorCode in formResult.validationErrorsByField.description" :key="errorCode">{{ $t(errorCode) }}</p>
                </div>
              </div>

              <div class="mb-3">
                <label for="price2">{{ $t('term.price') }}</label> <input type="text" class="form-control" id="price2" v-model="editForm.price" autocomplete="off">
                <div class="text-danger" v-if="formResult.validationErrorsByField && formResult.validationErrorsByField.price">
                  <p v-for="errorCode in formResult.validationErrorsByField.price" :key="errorCode">{{ $t(errorCode) }}</p>
                </div>
              </div>

              <div class="mb-3">
                <label for="category2">{{ $t('term.category') }}</label> <input type="text" class="form-control" id="category2" v-model="editForm.category" autocomplete="off">
                <div class="text-danger" v-if="formResult.validationErrorsByField && formResult.validationErrorsByField.category">
                  <p v-for="errorCode in formResult.validationErrorsByField.category" :key="errorCode">{{ $t(errorCode) }}</p>
                </div>
              </div>
            </div>
            <div class="modal-footer">
              <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">{{ $t('button.close') }}</button>
              <button type="button" class="btn btn-success" @click="edit()">{{ $t('button.edit') }}</button>
            </div>
          </div>
        </div>
      </div>

      <button type="button" class="btn btn-success" data-bs-toggle="modal" data-bs-target="#createWithTimeModal">{{ $t('button.createWithTime') }}</button>

      <div class="modal fade" id="createWithTimeModal" tabindex="-1" role="dialog" aria-labelledby="createWithTimeModalLabel" aria-hidden="true">
        <div class="modal-dialog" role="document">
          <div class="modal-content">
            <div class="modal-header">
              <h5 class="modal-title" id="createWithTimeModalLabel">{{ $t('button.createWithTime') }}</h5>
              <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
            </div>
            <div class="modal-body">
              <error-results :formResult="formResult"></error-results>

              <div class="mb-3">
                <label for="clientShortName">{{ $t('term.clientShortName') }}</label>
                <client-select id="clientShortName3" v-model="createForm.clientShortName"></client-select>
                <div class="text-danger" v-if="formResult.validationErrorsByField && formResult.validationErrorsByField.clientShortName">
                  <p v-for="errorCode in formResult.validationErrorsByField.clientShortName" :key="errorCode">{{ $t(errorCode) }}</p>
                </div>
              </div>

              <div class="mb-3">
                <label for="date">{{ $t('term.date') }}</label> <input type="text" class="form-control" id="date3" v-model="createForm.date" autocomplete="off">
                <div class="text-danger" v-if="formResult.validationErrorsByField && formResult.validationErrorsByField.date">
                  <p v-for="errorCode in formResult.validationErrorsByField.date" :key="errorCode">{{ $t(errorCode) }}</p>
                </div>
              </div>

              <div class="mb-3">
                <label for="description">{{ $t('term.description') }}</label> <input type="text" class="form-control" id="description3" v-model="createForm.description" autocomplete="off">
                <div class="text-danger" v-if="formResult.validationErrorsByField && formResult.validationErrorsByField.description">
                  <p v-for="errorCode in formResult.validationErrorsByField.description" :key="errorCode">{{ $t(errorCode) }}</p>
                </div>
              </div>

              <div class="mb-3">
                <label for="hours">{{ $t('term.hours') }}</label> <input type="text" class="form-control" id="hours3" v-model="createForm.hours" autocomplete="off">
                <div class="text-danger" v-if="formResult.validationErrorsByField && formResult.validationErrorsByField.hours">
                  <p v-for="errorCode in formResult.validationErrorsByField.hours" :key="errorCode">{{ $t(errorCode) }}</p>
                </div>
              </div>

              <div class="mb-3">
                <label for="minutes">{{ $t('term.minutes') }}</label> <input type="text" class="form-control" id="minutes3" v-model="createForm.minutes" autocomplete="off">
                <div class="text-danger" v-if="formResult.validationErrorsByField && formResult.validationErrorsByField.minutes">
                  <p v-for="errorCode in formResult.validationErrorsByField.minutes" :key="errorCode">{{ $t(errorCode) }}</p>
                </div>
              </div>

              <div class="mb-3">
                <label for="category">{{ $t('term.category') }}</label> <input type="text" class="form-control" id="category3" v-model="createForm.category" autocomplete="off">
                <div class="text-danger" v-if="formResult.validationErrorsByField && formResult.validationErrorsByField.category">
                  <p v-for="errorCode in formResult.validationErrorsByField.category" :key="errorCode">{{ $t(errorCode) }}</p>
                </div>
              </div>
            </div>
            <div class="modal-footer">
              <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">{{ $t('button.close') }}</button>
              <button type="button" class="btn btn-success" @click="createWithTime()">{{ $t('button.create') }}</button>
            </div>
          </div>
        </div>
      </div>

      <button type="button" class="btn btn-success" data-bs-toggle="modal" data-bs-target="#billPendingModal">{{ $t('button.billPending') }}</button>

      <div class="modal fade" id="billPendingModal" tabindex="-1" role="dialog" aria-labelledby="billPendingModalLabel" aria-hidden="true">
        <div class="modal-dialog" role="document">
          <div class="modal-content">
            <div class="modal-header">
              <h5 class="modal-title" id="cbillPendingModalLabel">{{ $t('button.billPending') }}</h5>
              <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
            </div>
            <div class="modal-body">
              <error-results :formResult="formResult"></error-results>

              <div class="mb-3">
                <label for="invoicePrefix">{{ $t('term.invoicePrefix') }}</label> <input type="text" class="form-control" id="invoicePrefix" v-model="billForm.invoicePrefix" autocomplete="off">
                <div class="text-danger" v-if="formResult.validationErrorsByField && formResult.validationErrorsByField.invoicePrefix">
                  <p v-for="errorCode in formResult.validationErrorsByField.invoicePrefix" :key="errorCode">{{ $t(errorCode) }}</p>
                </div>
              </div>
            </div>
            <div class="modal-footer">
              <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">{{ $t('button.close') }}</button>
              <button type="button" class="btn btn-success" @click="billPending()">{{ $t('button.billPending') }}</button>
            </div>
          </div>
        </div>
      </div>

      <div class="modal fade" id="billSelectedModal" tabindex="-1" role="dialog" aria-labelledby="billSelectedModalLabel" aria-hidden="true">
        <div class="modal-dialog" role="document">
          <div class="modal-content">
            <div class="modal-header">
              <h5 class="modal-title" id="cbillSelectedModalLabel">{{ $t('button.billSelected') }}</h5>
              <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
            </div>
            <div class="modal-body">
              <error-results :formResult="formResult"></error-results>

              <div class="mb-3">
                <label for="invoicePrefix">{{ $t('term.invoicePrefix') }}</label> <input type="text" class="form-control" id="invoicePrefix2" v-model="billForm.invoicePrefix" autocomplete="off">
                <div class="text-danger" v-if="formResult.validationErrorsByField && formResult.validationErrorsByField.invoicePrefix">
                  <p v-for="errorCode in formResult.validationErrorsByField.invoicePrefix" :key="errorCode">{{ $t(errorCode) }}</p>
                </div>
              </div>

              <p>{{(billForm.itemToBillIds && billForm.itemToBillIds.length) || 0}} {{$t('term.items')}}</p>
            </div>
            <div class="modal-footer">
              <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">{{ $t('button.close') }}</button>
              <button type="button" class="btn btn-success" @click="billSelected()">{{ $t('button.billSelected') }}</button>
            </div>
          </div>
        </div>
      </div>

      <pagination class="float-end" :pagination="pending.pagination" @changePage="refreshPending($event.pageId)"></pagination>

      <table class="table table-striped">
        <thead>
          <tr>
            <th></th>
            <th scope="col">{{ $t('term.client') }}</th>
            <th scope="col">{{ $t('term.date') }}</th>
            <th scope="col">{{ $t('term.category') }}</th>
            <th scope="col">{{ $t('term.description') }}</th>
            <th scope="col">{{ $t('term.price') }}</th>
            <th scope="col">{{ $t('term.actions') }}</th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="item in pending.items" :key="item.id">
            <td><input type="checkbox" v-model="item.selected"></td>
            <td>{{item.client.name}} ({{item.client.email}}) ({{item.client.lang}})</td>
            <td>{{item.dateFormatted}}</td>
            <td>{{item.category}}</td>
            <td>{{item.description}}</td>
            <td>{{item.priceFormatted}}</td>
            <td>
              <div>
                <button class="btn btn-sm btn-primary" data-bs-toggle="modal" @click="showEdit(item)" data-bs-target="#editModal">{{ $t('button.edit') }}</button>
                <button class="btn btn-sm btn-danger" @click="deleteOne(item)">{{ $t('button.delete') }}</button>
              </div>
            </td>
          </tr>
        </tbody>
      </table>

      <button type="button" class="float-end btn btn-success" @click="showBillSelected()">{{ $t('button.billSelected') }}</button>

      <hr />

      <h1>{{ $t('term.billed') }}</h1>

      <pagination class="float-end" :pagination="billed.pagination" @changePage="refreshBilled($event.pageId)"></pagination>

      <table class="table table-striped">
        <thead>
          <tr>
            <th scope="col">{{ $t('term.invoiceId') }}</th>
            <th scope="col">{{ $t('term.client') }}</th>
            <th scope="col">{{ $t('term.date') }}</th>
            <th scope="col">{{ $t('term.category') }}</th>
            <th scope="col">{{ $t('term.description') }}</th>
            <th scope="col">{{ $t('term.price') }}</th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="item in billed.items" :key="item.id">
            <td>{{item.invoiceId}}</td>
            <td>{{item.client.name}} ({{item.client.email}}) ({{item.client.lang}})</td>
            <td>{{item.dateFormatted}}</td>
            <td>{{item.category}}</td>
            <td>{{item.description}}</td>
            <td>{{item.priceFormatted}}</td>
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
import { dateNowDayOnly, priceToLong } from '@/utils/features'

export default {
  name: 'ItemsList',
  components: {
    ErrorResults,
    Pagination,
    ClientSelect
  },
  data() {
    return {
      billed: {
        queries: {},
        items: [],
        pagination: {
          currentPageUi: 1,
          totalPages: 1,
          firstPage: true,
          lastPage: true,
        },
      },
      pending: {
        queries: {},
        items: [],
        pagination: {
          currentPageUi: 1,
          totalPages: 1,
          firstPage: true,
          lastPage: true,
        },
      },
      billForm: {},
      createForm: {
        date: dateNowDayOnly(),
      },
      editForm: {
        clientShortName: null,
      },
      formResult: {},
    }
  },
  methods: {
    refresh(pageId) {
      if (pageId === undefined) {
        pageId = 1
      }

      this.refreshBilled(pageId)
      this.refreshPending(pageId)
    },
    refreshBilled(pageId) {
      if (pageId === undefined) {
        pageId = 1
      }

      this.billed.queries.pageId = pageId
      console.log('Items - Load Billed', this.billed.queries)

      http.get('/api/item/listBilled', this.billed.queries)
        .then(response => {
          this.billed.pagination = response.data.pagination
          this.billed.items = response.data.items || []
        })
        .catch(error => {
          console.error('Error loading billed items', error)
          this.billed.items = []
        })
    },
    refreshPending(pageId) {
      if (pageId === undefined) {
        pageId = 1
      }

      this.pending.queries.pageId = pageId
      console.log('Items - Load Pending', this.pending.queries)

      http.get('/api/item/listPending', this.pending.queries)
        .then(response => {
          this.pending.pagination = response.data.pagination
          this.pending.items = response.data.items || []
        })
        .catch(error => {
          console.error('Error loading pending items', error)
          this.pending.items = []
        })
    },
    showCreate() {
      this.formResult = {}
    },
    create() {
      this.formResult = {}
      const clonedForm = JSON.parse(JSON.stringify(this.createForm))
      clonedForm.price = priceToLong(clonedForm.price)
      console.log('Item - Create', clonedForm)

      http.post('/api/item', clonedForm)
        .then(response => {
          this.formResult = response.data
          if (response.data.success) {
            document.querySelector('#createModal .btn-secondary').click()
            http.showSuccess(this.$t('prompt.create.success', [this.createForm.description]))
            this.refreshPending(this.pending.queries.pageId)
          }
        })
        .catch(error => {
          console.error('Error creating item', error)
        })
    },
    createWithTime() {
      this.formResult = {}
      const clonedForm = JSON.parse(JSON.stringify(this.createForm))
      console.log('Item - Create With Time', clonedForm)

      http.post('/api/item/createWithTime', clonedForm)
        .then(response => {
          this.formResult = response.data
          if (response.data.success) {
            document.querySelector('#createWithTimeModal .btn-secondary').click()
            this.refreshPending(this.pending.queries.pageId)
          }
        })
        .catch(error => {
          console.error('Error creating item with time', error)
        })
    },
    billPending() {
      this.formResult = {}
      console.log('Item - Bill Pending', this.billForm)

      http.post('/api/item/billPending', this.billForm)
        .then(response => {
          this.formResult = response.data
          if (response.data.success) {
            document.querySelector('#billPendingModal .btn-secondary').click()
            this.refresh()
          }
        })
        .catch(error => {
          console.error('Error billing pending items', error)
        })
    },
    billSelected() {
      this.formResult = {}
      console.log('Item - Bill Selected', this.billForm)

      http.post('/api/item/billSomePending', this.billForm)
        .then(response => {
          this.formResult = response.data
          if (response.data.success) {
            document.querySelector('#billSelectedModal .btn-secondary').click()
            this.refresh()
          }
        })
        .catch(error => {
          console.error('Error billing selected items', error)
        })
    },
    deleteOne(item) {
      if (confirm(this.$t('prompt.delete.confirm', [item.id]))) {
        console.log('Item - Pending - Delete', item.id)

        http.delete('/api/item/' + item.id)
          .then(() => {
            http.showSuccess(this.$t('prompt.delete.success', [item.id]))
            this.refresh()
          })
          .catch(error => {
            console.error('Error deleting item', error)
          })
      }
    },
    showBillSelected() {
      this.formResult = {}
      this.billForm = {
        itemToBillIds: []
      }
      this.pending.items.forEach(element => {
        if (element.selected) {
          this.billForm.itemToBillIds.push(element.id)
        }
      })
      console.log(this.billForm.itemToBillIds)
      // Use Bootstrap's modal API to show the modal
      const modal = document.querySelector('#billSelectedModal')
      if (modal) {
        // Use Bootstrap's jQuery API if available
        if (window.jQuery) {
          window.jQuery(modal).modal('show')
        } else {
          // Fallback to data-toggle attribute
          modal.setAttribute('data-toggle', 'modal')
          modal.click()
        }
      }
    },
    showEdit(item) {
      this.formResult = {}
      Object.assign(this.editForm, item)
      this.editForm.id = item.id
      this.editForm.price = this.editForm.priceFormatted
      this.editForm.date = this.editForm.dateFormatted
      if (item.client) {
        this.editForm.clientShortName = item.client.shortName
      }
    },
    edit() {
      this.formResult = {}
      const clonedForm = JSON.parse(JSON.stringify(this.editForm))
      clonedForm.price = priceToLong(clonedForm.price)
      console.log('Item - Pending - Edit', clonedForm)

      http.put('/api/item/' + this.editForm.id, clonedForm)
        .then(response => {
          this.formResult = response.data
          if (response.data.success) {
            document.querySelector('#editModal .btn-secondary').click()
            http.showSuccess(this.$t('prompt.edit.success', [this.editForm.id]))
            this.refresh()
          }
        })
        .catch(error => {
          console.error('Error editing item', error)
        })
    },
  },
  mounted() {
    this.refresh()
  }
}
</script>

<style scoped>
</style>
