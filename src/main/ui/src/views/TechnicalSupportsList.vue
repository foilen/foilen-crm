<template>
  <div class="row">
    <div class="col-12">
      <button type="button" class="btn btn-success" data-toggle="modal" @click="showCreate()" data-target="#createModal">{{ $t('button.create') }}</button>

      <div class="modal fade" id="createModal" tabindex="-1" role="dialog" aria-labelledby="createModalLabel" aria-hidden="true">
        <div class="modal-dialog" role="document">
          <div class="modal-content">
            <div class="modal-header">
              <h5 class="modal-title" id="createModalLabel">{{ $t('button.create') }}</h5>
              <button type="button" class="close" data-dismiss="modal" aria-label="Close">
                <span aria-hidden="true">&times;</span>
              </button>
            </div>
            <div class="modal-body">
              <error-results :formResult="formResult"></error-results>

              <div class="form-group">
                <label for="sid">{{ $t('term.sid') }}</label> <input type="text" class="form-control" id="sid" v-model="createForm.sid" autocomplete="off">
                <div class="text-danger" v-if="formResult.validationErrorsByField && formResult.validationErrorsByField.sid">
                  <p v-for="errorCode in formResult.validationErrorsByField.sid" :key="errorCode">{{ $t(errorCode) }}</p>
                </div>
              </div>

              <div class="form-group">
                <label for="pricePerHour">{{ $t('term.pricePerHour') }}</label> <input type="text" class="form-control" id="pricePerHour" v-model="createForm.pricePerHour" autocomplete="off">
                <div class="text-danger" v-if="formResult.validationErrorsByField && formResult.validationErrorsByField.pricePerHour">
                  <p v-for="errorCode in formResult.validationErrorsByField.pricePerHour" :key="errorCode">{{ $t(errorCode) }}</p>
                </div>
              </div>
            </div>
            <div class="modal-footer">
              <button type="button" class="btn btn-secondary" data-dismiss="modal">{{ $t('button.close') }}</button>
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
              <button type="button" class="close" data-dismiss="modal" aria-label="Close">
                <span aria-hidden="true">&times;</span>
              </button>
            </div>
            <div class="modal-body">
              <error-results :formResult="formResult"></error-results>

              <div class="form-group">
                <label for="sid2">{{ $t('term.sid') }}</label> <input type="text" class="form-control" id="sid2" v-model="editForm.sid" autocomplete="off">
                <div class="text-danger" v-if="formResult.validationErrorsByField && formResult.validationErrorsByField.sid">
                  <p v-for="errorCode in formResult.validationErrorsByField.sid" :key="errorCode">{{ $t(errorCode) }}</p>
                </div>
              </div>

              <div class="form-group">
                <label for="pricePerHour2">{{ $t('term.pricePerHour') }}</label> <input type="text" class="form-control" id="pricePerHour2" v-model="editForm.pricePerHour" autocomplete="off">
                <div class="text-danger" v-if="formResult.validationErrorsByField && formResult.validationErrorsByField.pricePerHour">
                  <p v-for="errorCode in formResult.validationErrorsByField.pricePerHour" :key="errorCode">{{ $t(errorCode) }}</p>
                </div>
              </div>
            </div>
            <div class="modal-footer">
              <button type="button" class="btn btn-secondary" data-dismiss="modal">{{ $t('button.close') }}</button>
              <button type="button" class="btn btn-success" @click="edit()">{{ $t('button.edit') }}</button>
            </div>
          </div>
        </div>
      </div>

      <pagination class="float-right" :pagination="pagination" @changePage="refresh($event.pageId)"></pagination>

      <table class="table table-striped">
        <thead>
          <tr>
            <th scope="col">{{ $t('term.sid') }}</th>
            <th scope="col">{{ $t('term.pricePerHour') }}</th>
            <th scope="col">{{ $t('term.actions') }}</th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="item in items" :key="item.sid">
            <td>{{item.sid}}</td>
            <td>{{item.pricePerHourFormatted}}$</td>
            <td>
              <div>
                <button class="btn btn-sm btn-primary" data-toggle="modal" @click="showEdit(item)" data-target="#editModal">{{ $t('button.edit') }}</button>
                <button class="btn btn-sm btn-danger" data-toggle="modal" @click="deleteOne(item)">{{ $t('button.delete') }}</button>
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
import http from '@/utils/http'
import { priceToLong } from '@/utils/features'

export default {
  name: 'TechnicalSupportsList',
  components: {
    ErrorResults,
    Pagination
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
      createForm: {},
      editForm: {},
      formResult: {},
    }
  },
  methods: {
    refresh(pageId) {
      if (pageId === undefined) {
        pageId = 1
      }
      this.queries.pageId = pageId
      console.log('Technical Supports - Load', this.queries)

      http.get('/api/technicalSupport/listAll', this.queries)
        .then(response => {
          this.pagination = response.data.pagination
          this.items = response.data.items || []
        })
        .catch(error => {
          console.error('Error loading technical supports', error)
          this.items = []
        })
    },
    showCreate() {
      this.formResult = {}
    },
    create() {
      this.formResult = {}
      const clonedForm = JSON.parse(JSON.stringify(this.createForm))
      clonedForm.pricePerHour = priceToLong(clonedForm.pricePerHour)
      console.log('Technical Support - Create', clonedForm)

      http.post('/api/technicalSupport', clonedForm)
        .then(response => {
          this.formResult = response.data
          if (response.data.success) {
            document.querySelector('#createModal .btn-secondary').click()
            http.showSuccess(this.$t('prompt.create.success', [this.createForm.sid]))
            this.refresh(this.queries.pageId)
          }
        })
        .catch(error => {
          console.error('Error creating technical support', error)
        })
    },
    deleteOne(item) {
      if (confirm(this.$t('prompt.delete.confirm', [item.sid]))) {
        console.log('Technical Support - Delete', item.sid)

        http.delete('/api/technicalSupport/' + item.sid)
          .then(() => {
            http.showSuccess(this.$t('prompt.delete.success', [item.sid]))
            this.refresh(this.queries.pageId)
          })
          .catch(error => {
            console.error('Error deleting technical support', error)
          })
      }
    },
    showEdit(item) {
      this.formResult = {}
      Object.assign(this.editForm, item)
      this.editForm.id = item.sid
      this.editForm.pricePerHour = this.editForm.pricePerHourFormatted
    },
    edit() {
      this.formResult = {}
      const clonedForm = JSON.parse(JSON.stringify(this.editForm))
      clonedForm.pricePerHour = priceToLong(clonedForm.pricePerHour)
      console.log('Technical Support - Edit', clonedForm)

      http.put('/api/technicalSupport/' + clonedForm.id, clonedForm)
        .then(response => {
          this.formResult = response.data
          if (response.data.success) {
            document.querySelector('#editModal .btn-secondary').click()
            http.showSuccess(this.$t('prompt.edit.success', [this.editForm.sid]))
            this.refresh(this.queries.pageId)
          }
        })
        .catch(error => {
          console.error('Error editing technical support', error)
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