const RecurrentItemsList = Vue.component('recurrent_items-list', function(resolve, reject) {

  const url = 'views/recurrentItemsList.html'

  jQuery.get(url).done(function(htmlTemplate) {

    resolve({
      data : function() {
        return {
          queries : {},
          formResult: {},
          form: {},
          editForm: {},
          items : [],
          frequencies: ['Monthly', 'Yearly', 'Unknown'],
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
          console.log('Recurrent Items - Load', c.queries)
          httpGetQueries('/api/recurrentItem/listAll', c.queries, function(data) {
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
          console.log('Recurrent Item - Create', c.form)
          httpPost('/api/recurrentItem', c.form, function(data) {
            c.formResult = data
            if (data.success) {
              jQuery('#createModal .btn-secondary').click()
              successShow(c.$t('prompt.create.success', [ 'Recurrent Item' ]))
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
          console.log('Recurrent Item - Edit', c.editForm)

          httpPut('/api/recurrentItem/' + c.editForm.id, c.editForm, function(data) {
            c.formResult = data
            if (data.success) {
              jQuery('#editModal .btn-secondary').click()
              successShow(c.$t('prompt.edit.success', [ c.editForm.id ]))
              c.refresh()
            }
          })
        },
        deleteOne: function(item) {
          console.log(item)
          if (confirm(this.$t('prompt.delete.confirm', [ item.id ]))) {
            var c = this
            console.log('Recurrent Item - Delete', item.id)

            httpDelete('/api/recurrentItem/' + item.id, function() {
              successShow(c.$t('prompt.delete.success', [ item.id ]))
              c.refresh()
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
