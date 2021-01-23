const TransactionsList = Vue.component('transactions-list', function(resolve, reject) {

  const url = 'views/transactionsList.html'

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
          form : {
            date : dateNowDayOnly(),
          },
          formResult : {},
        }
      },
      props : [],
      computed : {},
      methods : {
        create : function() {
          var c = this
          c.formResult = {}
          var clonedForm = JSON.parse(JSON.stringify(c.form))
          clonedForm.price = priceToLong(clonedForm.price)
          console.log('Transaction Payment - Create', clonedForm)
          httpPost('/api/transaction/payment', clonedForm, function(data) {
            c.formResult = data
            if (data.success) {
              jQuery('#createModal .btn-secondary').click()
              c.refresh(c.queries.pageId)
            }
          })
        },
        refresh : function(pageId) {
          if (pageId === undefined) {
            pageId = 1
          }
          var c = this
          c.queries.pageId = pageId
          console.log('Transactions - Load', c.queries)
          httpGetQueries('/api/transaction/listAll', c.queries, function(data) {
            c.pagination = data.pagination
            c.items = data.items
            if (c.items == null) {
              c.items = []
            }
          })
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
