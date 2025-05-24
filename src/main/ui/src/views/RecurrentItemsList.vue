<template>
  <div class="row">
    <div class="col-12">
      <button type="button" class="btn btn-success" data-bs-toggle="modal" @click="showCreate()" data-bs-target="#createModal">{{ $t('button.create') }}</button>

      <pagination class="float-end" :pagination="pagination" @changePage="refresh($event.pageId)"></pagination>

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
                <label for="calendarUnit">{{ $t('term.frequency') }}</label> 
                <select class="form-control" id="calendarUnit" v-model="createForm.calendarUnit">
                  <option v-for="frequency in frequencies" :key="frequency[0]" :value="frequency[0]">{{ $t(frequency[1]) }}</option>
                </select>
                <div class="text-danger" v-if="formResult.validationErrorsByField && formResult.validationErrorsByField.calendarUnit">
                  <p v-for="errorCode in formResult.validationErrorsByField.calendarUnit" :key="errorCode">{{ $t(errorCode) }}</p>
                </div>
              </div>
              <div class="mb-3">
                <label for="delta">{{ $t('term.delta') }}</label> <input type="text" class="form-control" id="delta" v-model="createForm.delta" autocomplete="off">
                <div class="text-danger" v-if="formResult.validationErrorsByField && formResult.validationErrorsByField.delta">
                  <p v-for="errorCode in formResult.validationErrorsByField.delta" :key="errorCode">{{ $t(errorCode) }}</p>
                </div>
              </div>
              <div class="mb-3">
                <label for="nextGenerationDate">{{ $t('term.nextGenerationDate') }}</label> <input type="text" class="form-control" id="nextGenerationDate" v-model="createForm.nextGenerationDate" autocomplete="off">
                <div class="text-danger" v-if="formResult.validationErrorsByField && formResult.validationErrorsByField.nextGenerationDate">
                  <p v-for="errorCode in formResult.validationErrorsByField.nextGenerationDate" :key="errorCode">{{ $t(errorCode) }}</p>
                </div>
              </div>
              <div class="mb-3">
                <label for="clientShortName">{{ $t('term.clientShortName') }}</label>
                <client-select id="clientShortName" v-model="createForm.clientShortName"></client-select>
                <div class="text-danger" v-if="formResult.validationErrorsByField && formResult.validationErrorsByField.clientShortName">
                  <p v-for="errorCode in formResult.validationErrorsByField.clientShortName" :key="errorCode">{{ $t(errorCode) }}</p>
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
                <label for="calendarUnit2">{{ $t('term.frequency') }}</label> 
                <select class="form-control" id="calendarUnit2" v-model="editForm.calendarUnit">
                  <option v-for="frequency in frequencies" :key="frequency[0]" :value="frequency[0]">{{ $t(frequency[1]) }}</option>
                </select>
                <div class="text-danger" v-if="formResult.validationErrorsByField && formResult.validationErrorsByField.calendarUnit">
                  <p v-for="errorCode in formResult.validationErrorsByField.calendarUnit" :key="errorCode">{{ $t(errorCode) }}</p>
                </div>
              </div>
              <div class="mb-3">
                <label for="delta2">{{ $t('term.delta') }}</label> <input type="text" class="form-control" id="delta2" v-model="editForm.delta" autocomplete="off">
                <div class="text-danger" v-if="formResult.validationErrorsByField && formResult.validationErrorsByField.delta">
                  <p v-for="errorCode in formResult.validationErrorsByField.delta" :key="errorCode">{{ $t(errorCode) }}</p>
                </div>
              </div>
              <div class="mb-3">
                <label for="nextGenerationDate2">{{ $t('term.nextGenerationDate') }}</label> <input type="text" class="form-control" id="nextGenerationDate2" v-model="editForm.nextGenerationDate" autocomplete="off">
                <div class="text-danger" v-if="formResult.validationErrorsByField && formResult.validationErrorsByField.nextGenerationDate">
                  <p v-for="errorCode in formResult.validationErrorsByField.nextGenerationDate" :key="errorCode">{{ $t(errorCode) }}</p>
                </div>
              </div>
              <div class="mb-3">
                <label for="clientShortName2">{{ $t('term.clientShortName') }}</label>
                <client-select id="clientShortName2" v-model="editForm.clientShortName"></client-select>
                <div class="text-danger" v-if="formResult.validationErrorsByField && formResult.validationErrorsByField.clientShortName">
                  <p v-for="errorCode in formResult.validationErrorsByField.clientShortName" :key="errorCode">{{ $t(errorCode) }}</p>
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

      <table class="table table-striped">
        <thead>
          <tr>
            <th scope="col">{{ $t('term.client') }}</th>
            <th scope="col">{{ $t('term.category') }}</th>
            <th scope="col">{{ $t('term.description') }}</th>
            <th scope="col">{{ $t('term.price') }}</th>
            <th scope="col">{{ $t('term.frequency') }}</th>
            <th scope="col">{{ $t('term.delta') }}</th>
            <th scope="col">{{ $t('term.nextGenerationDate') }}</th>
            <th scope="col">{{ $t('term.actions') }}</th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="item in items" :key="item.id">
            <td>{{item.client.name}} ({{item.client.email}}) ({{item.client.lang}})</td>
            <td>{{item.category}}</td>
            <td>{{item.description}}</td>
            <td>{{item.priceFormatted}}</td>
            <td>{{$t(item.calendarUnitCode)}}</td>
            <td>{{item.delta}}</td>
            <td>{{item.nextGenerationDateFormatted}}</td>
            <td>
              <div>
                <button class="btn btn-sm btn-primary" data-bs-toggle="modal" @click="showEdit(item)" data-bs-target="#editModal">{{ $t('button.edit') }}</button>
                <button class="btn btn-sm btn-danger" @click="deleteOne(item)">{{ $t('button.delete') }}</button>
              </div>
            </td>
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
  name: 'RecurrentItemsList',
  components: {
    ErrorResults,
    Pagination,
    ClientSelect
  },
  data() {
    return {
      queries: {},
      items: [],
      frequencies: [
        [2, 'recurrence.monthly'],
        [1, 'recurrence.yearly'],
      ],
      pagination: {
        currentPageUi: 1,
        totalPages: 1,
        firstPage: true,
        lastPage: true,
      },
      createForm: {
        nextGenerationDate: dateNowDayOnly(),
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
      this.queries.pageId = pageId
      console.log('Recurrent Items - Load', this.queries)

      http.get('/api/recurrentItem/listAll', this.queries)
        .then(response => {
          this.pagination = response.data.pagination
          this.items = response.data.items || []
        })
        .catch(error => {
          console.error('Error loading recurrent items', error)
          this.items = []
        })
    },
    showCreate() {
      this.formResult = {}
    },
    create() {
      this.formResult = {}
      const clonedForm = JSON.parse(JSON.stringify(this.createForm))
      clonedForm.price = priceToLong(clonedForm.price)
      console.log('Recurrent Item - Create', clonedForm)

      http.post('/api/recurrentItem', clonedForm)
        .then(response => {
          this.formResult = response.data
          if (response.data.success) {
            document.querySelector('#createModal .btn-secondary').click()
            http.showSuccess(this.$t('prompt.create.success', [this.createForm.description]))
            this.refresh(this.queries.pageId)
          }
        })
        .catch(error => {
          console.error('Error creating recurrent item', error)
        })
    },
    deleteOne(item) {
      if (confirm(this.$t('prompt.delete.confirm', [item.id]))) {
        console.log('Recurrent Item - Delete', item.id)

        http.delete('/api/recurrentItem/' + item.id)
          .then(() => {
            http.showSuccess(this.$t('prompt.delete.success', [item.description]))
            this.refresh()
          })
          .catch(error => {
            console.error('Error deleting recurrent item', error)
          })
      }
    },
    showEdit(item) {
      this.formResult = {}
      Object.assign(this.editForm, item)
      this.editForm.id = item.id
      this.editForm.nextGenerationDate = item.nextGenerationDateFormatted
      this.editForm.price = this.editForm.priceFormatted
      if (item.client) {
        this.editForm.clientShortName = item.client.shortName
      }
    },
    edit() {
      this.formResult = {}
      const clonedForm = JSON.parse(JSON.stringify(this.editForm))
      clonedForm.price = priceToLong(clonedForm.price)
      console.log('Recurrent Item - Edit', clonedForm)

      http.put('/api/recurrentItem/' + clonedForm.id, clonedForm)
        .then(response => {
          this.formResult = response.data
          if (response.data.success) {
            document.querySelector('#editModal .btn-secondary').click()
            http.showSuccess(this.$t('prompt.edit.success', [this.editForm.description]))
            this.refresh()
          }
        })
        .catch(error => {
          console.error('Error editing recurrent item', error)
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
