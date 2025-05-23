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
                <label for="name">{{ $t('term.name') }}</label> <input type="text" class="form-control" id="name" v-model="createForm.name" autocomplete="off">
                <div class="text-danger" v-if="formResult.validationErrorsByField && formResult.validationErrorsByField.name">
                  <p v-for="errorCode in formResult.validationErrorsByField.name" :key="errorCode">{{ $t(errorCode) }}</p>
                </div>
              </div>

              <div class="form-group">
                <label for="shortName">{{ $t('term.shortName') }}</label> <input type="text" class="form-control" id="shortName" v-model="createForm.shortName" autocomplete="off">
                <div class="text-danger" v-if="formResult.validationErrorsByField && formResult.validationErrorsByField.shortName">
                  <p v-for="errorCode in formResult.validationErrorsByField.shortName" :key="errorCode">{{ $t(errorCode) }}</p>
                </div>
              </div>

              <div class="form-group">
                <label for="contactName">{{ $t('term.contactName') }}</label> <input type="text" class="form-control" id="contactName" v-model="createForm.contactName" autocomplete="off">
                <div class="text-danger" v-if="formResult.validationErrorsByField && formResult.validationErrorsByField.contactName">
                  <p v-for="errorCode in formResult.validationErrorsByField.contactName" :key="errorCode">{{ $t(errorCode) }}</p>
                </div>
              </div>

              <div class="form-group">
                <label for="email">{{ $t('term.email') }}</label> <input type="text" class="form-control" id="email" v-model="createForm.email" autocomplete="off">
                <div class="text-danger" v-if="formResult.validationErrorsByField && formResult.validationErrorsByField.email">
                  <p v-for="errorCode in formResult.validationErrorsByField.email" :key="errorCode">{{ $t(errorCode) }}</p>
                </div>
              </div>

              <div class="form-group">
                <label for="address">{{ $t('term.address') }}</label> <input type="text" class="form-control" id="address" v-model="createForm.address" autocomplete="off">
                <div class="text-danger" v-if="formResult.validationErrorsByField && formResult.validationErrorsByField.address">
                  <p v-for="errorCode in formResult.validationErrorsByField.address" :key="errorCode">{{ $t(errorCode) }}</p>
                </div>
              </div>

              <div class="form-group">
                <label for="tel">{{ $t('term.tel') }}</label> <input type="text" class="form-control" id="tel" v-model="createForm.tel" autocomplete="off">
                <div class="text-danger" v-if="formResult.validationErrorsByField && formResult.validationErrorsByField.tel">
                  <p v-for="errorCode in formResult.validationErrorsByField.tel" :key="errorCode">{{ $t(errorCode) }}</p>
                </div>
              </div>

              <div class="form-group">
                <label for="mainSite">{{ $t('term.mainSite') }}</label> <input type="text" class="form-control" id="mainSite" v-model="createForm.mainSite" autocomplete="off">
                <div class="text-danger" v-if="formResult.validationErrorsByField && formResult.validationErrorsByField.mainSite">
                  <p v-for="errorCode in formResult.validationErrorsByField.mainSite" :key="errorCode">{{ $t(errorCode) }}</p>
                </div>
              </div>

              <div class="form-group">
                <label for="lang">{{ $t('term.lang') }}</label> <select class="form-control" id="lang" v-model="createForm.lang" autocomplete="off">
                  <option value=""></option>
                  <option>FR</option>
                  <option>EN</option>
                </select>
                <div class="text-danger" v-if="formResult.validationErrorsByField && formResult.validationErrorsByField.lang">
                  <p v-for="errorCode in formResult.validationErrorsByField.lang" :key="errorCode">{{ $t(errorCode) }}</p>
                </div>
              </div>

              <div class="form-group">
                <label for="technicalSupportSid">{{ $t('term.technicalSupportSid') }}</label>
                <technical-support-select id="technicalSupportSid" v-model="createForm.technicalSupportSid"></technical-support-select>
                <div class="text-danger" v-if="formResult.validationErrorsByField && formResult.validationErrorsByField.technicalSupportSid">
                  <p v-for="errorCode in formResult.validationErrorsByField.technicalSupportSid" :key="errorCode">{{ $t(errorCode) }}</p>
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
                <label for="name">{{ $t('term.name') }}</label> <input type="text" class="form-control" id="name2" v-model="editForm.name" autocomplete="off">
                <div class="text-danger" v-if="formResult.validationErrorsByField && formResult.validationErrorsByField.name">
                  <p v-for="errorCode in formResult.validationErrorsByField.name" :key="errorCode">{{ $t(errorCode) }}</p>
                </div>
              </div>

              <div class="form-group">
                <label for="shortName">{{ $t('term.shortName') }}</label> <input type="text" class="form-control" id="shortName2" v-model="editForm.shortName" autocomplete="off">
                <div class="text-danger" v-if="formResult.validationErrorsByField && formResult.validationErrorsByField.shortName">
                  <p v-for="errorCode in formResult.validationErrorsByField.shortName" :key="errorCode">{{ $t(errorCode) }}</p>
                </div>
              </div>

              <div class="form-group">
                <label for="contactName">{{ $t('term.contactName') }}</label> <input type="text" class="form-control" id="contactName2" v-model="editForm.contactName" autocomplete="off">
                <div class="text-danger" v-if="formResult.validationErrorsByField && formResult.validationErrorsByField.contactName">
                  <p v-for="errorCode in formResult.validationErrorsByField.contactName" :key="errorCode">{{ $t(errorCode) }}</p>
                </div>
              </div>

              <div class="form-group">
                <label for="email">{{ $t('term.email') }}</label> <input type="text" class="form-control" id="email2" v-model="editForm.email" autocomplete="off">
                <div class="text-danger" v-if="formResult.validationErrorsByField && formResult.validationErrorsByField.email">
                  <p v-for="errorCode in formResult.validationErrorsByField.email" :key="errorCode">{{ $t(errorCode) }}</p>
                </div>
              </div>

              <div class="form-group">
                <label for="address">{{ $t('term.address') }}</label> <input type="text" class="form-control" id="address2" v-model="editForm.address" autocomplete="off">
                <div class="text-danger" v-if="formResult.validationErrorsByField && formResult.validationErrorsByField.address">
                  <p v-for="errorCode in formResult.validationErrorsByField.address" :key="errorCode">{{ $t(errorCode) }}</p>
                </div>
              </div>

              <div class="form-group">
                <label for="tel">{{ $t('term.tel') }}</label> <input type="text" class="form-control" id="tel2" v-model="editForm.tel" autocomplete="off">
                <div class="text-danger" v-if="formResult.validationErrorsByField && formResult.validationErrorsByField.tel">
                  <p v-for="errorCode in formResult.validationErrorsByField.tel" :key="errorCode">{{ $t(errorCode) }}</p>
                </div>
              </div>

              <div class="form-group">
                <label for="mainSite">{{ $t('term.mainSite') }}</label> <input type="text" class="form-control" id="mainSite2" v-model="editForm.mainSite" autocomplete="off">
                <div class="text-danger" v-if="formResult.validationErrorsByField && formResult.validationErrorsByField.mainSite">
                  <p v-for="errorCode in formResult.validationErrorsByField.mainSite" :key="errorCode">{{ $t(errorCode) }}</p>
                </div>
              </div>

              <div class="form-group">
                <label for="lang">{{ $t('term.lang') }}</label> <select class="form-control" id="lang2" v-model="editForm.lang" autocomplete="off">
                <option value=""></option>
                <option>FR</option>
                <option>EN</option>
              </select>
                <div class="text-danger" v-if="formResult.validationErrorsByField && formResult.validationErrorsByField.lang">
                  <p v-for="errorCode in formResult.validationErrorsByField.lang" :key="errorCode">{{ $t(errorCode) }}</p>
                </div>
              </div>

              <div class="form-group">
                <label for="technicalSupportSid2">{{ $t('term.technicalSupportSid') }}</label>
                <technical-support-select id="technicalSupportSid2" v-model="editForm.technicalSupportSid"></technical-support-select>
                <div class="text-danger" v-if="formResult.validationErrorsByField && formResult.validationErrorsByField.technicalSupportSid">
                  <p v-for="errorCode in formResult.validationErrorsByField.technicalSupportSid" :key="errorCode">{{ $t(errorCode) }}</p>
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
            <th scope="col">{{ $t('term.name') }}</th>
            <th scope="col">{{ $t('term.shortName') }}</th>
            <th scope="col">{{ $t('term.contactName') }}</th>
            <th scope="col">{{ $t('term.email') }}</th>
            <th scope="col">{{ $t('term.address') }}</th>
            <th scope="col">{{ $t('term.tel') }}</th>
            <th scope="col">{{ $t('term.mainSite') }}</th>
            <th scope="col">{{ $t('term.lang') }}</th>
            <th scope="col">{{ $t('term.sid') }}</th>
            <th scope="col">{{ $t('term.pricePerHour') }}</th>
            <th scope="col">{{ $t('term.actions') }}</th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="item in items" :key="item.shortName">
            <td>{{item.name}}</td>
            <td>{{item.shortName}}</td>
            <td>{{item.contactName}}</td>
            <td><a :href="'mailto:' + item.email">{{item.email}}</a></td>
            <td>{{item.address}}</td>
            <td>{{item.tel}}</td>
            <td><a :href="item.mainSite" target="_blank">{{item.mainSite}}</a></td>
            <td>{{item.lang}}</td>
            <td>{{item.technicalSupport ? item.technicalSupport.sid : ''}}</td>
            <td>{{item.technicalSupport ? item.technicalSupport.pricePerHourFormatted + '$' : ''}}</td>
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
import TechnicalSupportSelect from '@/components/TechnicalSupportSelect.vue'
import http from '@/utils/http'

export default {
  name: 'ClientsList',
  components: {
    ErrorResults,
    Pagination,
    TechnicalSupportSelect
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
      editForm: {
        clientShortName: null,
        technicalSupportSid: null,
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
      console.log('Clients - Load', this.queries)

      http.get('/api/client/listAll', this.queries)
        .then(response => {
          this.pagination = response.data.pagination
          this.items = response.data.items || []
        })
        .catch(error => {
          console.error('Error loading clients', error)
          this.items = []
        })
    },
    showCreate() {
      this.formResult = {}
    },
    create() {
      this.formResult = {}
      console.log('Clients - Create', this.createForm)

      http.post('/api/client', this.createForm)
        .then(response => {
          this.formResult = response.data
          if (response.data.success) {
            document.querySelector('#createModal .btn-secondary').click()
            http.showSuccess(this.$t('prompt.create.success', [this.createForm.shortName]))
            this.refresh(this.queries.pageId)
          }
        })
        .catch(error => {
          console.error('Error creating client', error)
        })
    },
    deleteOne(item) {
      if (confirm(this.$t('prompt.delete.confirm', [item.name]))) {
        console.log('Client - Delete', item.name)

        http.delete('/api/client/' + item.shortName)
          .then(() => {
            http.showSuccess(this.$t('prompt.delete.success', [item.shortName]))
            this.refresh(this.queries.pageId)
          })
          .catch(error => {
            console.error('Error deleting client', error)
          })
      }
    },
    showEdit(item) {
      this.formResult = {}
      Object.assign(this.editForm, item)
      this.editForm.clientShortName = item.shortName
      if (item.technicalSupport) {
        this.editForm.technicalSupportSid = item.technicalSupport.sid
      }
    },
    edit() {
      this.formResult = {}
      console.log('Clients - Edit', this.editForm)

      http.put('/api/client/' + this.editForm.clientShortName, this.editForm)
        .then(response => {
          this.formResult = response.data
          if (response.data.success) {
            document.querySelector('#editModal .btn-secondary').click()
            http.showSuccess(this.$t('prompt.edit.success', [this.editForm.name]))
            this.refresh(this.queries.pageId)
          }
        })
        .catch(error => {
          console.error('Error editing client', error)
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