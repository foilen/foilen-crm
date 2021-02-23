const TechnicalSupportsList = Vue.component('technical_support-list', function(resolve, reject) {

  const url = 'views/technicalSupportsList.html'

  jQuery.get(url).done(function(htmlTemplate) {

    resolve({
      data : function() {
        return {
          queries : {},
          formResult: {},
          form: {},
          editForm: {},
          items : [],
          pagination : {
            currentPageUi : 1,
            totalPages : 1,
            firstPage : true,
            lastPage : true,
          },
        }
      },
      props : [],
      computed : {},
      methods : {
        refresh : function(pageId) {
          if (pageId === undefined) {
            pageId = 1
          }
          var c = this
          c.queries.pageId = pageId
          console.log('Technical Supports - Load', c.queries)
          httpGetQueries('/api/technicalSupport/listAll', c.queries, function(data) {
            c.pagination = data.pagination
            c.items = data.items
            if (c.items == null) {
              c.items = []
            }
          })
        },
        create: function() {
          var c =  this
          c.formResult = {}
          console.log('Technical Support - Create', c.form)
          httpPost('/api/technicalSupport', c.form, function(data) {
            c.formResult = data
            if (data.success) {
              jQuery('#createModal .btn-secondary').click()
              c.refresh(c.queries.pageId)
            }
          })
        },
        setupEdit: function(item) {
          var c = this
          c.formResult = {}
          c.editForm = JSON.parse(JSON.stringify(item))
        },
        edit : function() {
          var c = this
          c.formResult = {}
          console.log('Technical Support - Edit', c.editForm)

          httpPut('/api/technicalSupport/' + c.editForm.sid, c.editForm, function(data) {
            c.formResult = data
            if (data.success) {
              jQuery('#editModal .btn-secondary').click()
              successShow(c.$t('prompt.edit.success', [ c.editForm.sid ]))
              c.refresh(c.queries.pageId)
            }
          })
        },
        deleteOne: function(item) {
          if (confirm(this.$t('prompt.delete.confirm', [ item.sid ]))) {
            var c = this
            console.log('Technical Support - Delete', item.sid)

            httpDelete('/api/technicalSupport/' + item.sid, function() {
              successShow(c.$t('prompt.delete.success', [item.sid ]))
              c.refresh(c.queries.pageId)
            })
          }
        }
      },
      mounted : function() {
        this.refresh()
      },
      template : htmlTemplate
    })

  }).fail(function(jqXHR, textStatus, errorThrown) {
    var escapedResponseText = new Option(jqXHR.responseText).innerHTML;
    var error = 'Could not get the template ' + url + ' . Error: ' + textStatus + ' ' + errorThrown + '<br/>' + escapedResponseText;
    errorShow(error)
    reject(error)
  })

})
