const ClientsList = Vue.component('clients-list', function(resolve, reject) {

  const url = 'views/clientsList.html'

  jQuery.get(url).done(function(htmlTemplate) {

    resolve({
      data : function() {
        return {
          queries : {},
          items : [],
          pagination : {
            currentPageUi : 1,
            totalPages : 1,
            firstPage : true,
            lastPage : true,
          },
          form : {},
          editForm: {},
          tempForm: {},
          formResult : {},
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
          console.log('Clients - Load', c.queries)
          httpGetQueries('/api/client/listAll', c.queries, function(data) {
            c.pagination = data.pagination
            c.items = data.items
            if (c.items == null) {
              c.items = []
            }
          })
        },
        create : function() {
          var c = this
          c.formResult = {}
          console.log('Clients - Create', c.form)
          httpPost('/api/client', c.form, function(data) {
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
          console.log('Clients - Edit', c.editForm)

          httpPut('/api/client/' + c.editForm.id, c.editForm, function(data) {
            c.formResult = data
            if (data.success) {
              jQuery('#editModal .btn-secondary').click()
              successShow(c.$t('prompt.delete.success', [ c.editForm.name ]))
              c.refresh(c.queries.pageId)
            }
          })
        },
        deleteOne: function(item) {
          if (confirm(this.$t('prompt.delete.confirm', [ item.name ]))) {
            var c = this
            console.log('Client - Delete', item.name)

            httpDelete('/api/client/' + item.id, function() {
              successShow(c.$t('prompt.delete.success', [item.name ]))
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
