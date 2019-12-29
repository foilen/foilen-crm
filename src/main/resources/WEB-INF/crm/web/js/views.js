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

const Home = Vue.component('home', function(resolve, reject) {

  const url = 'views/home.html'

  jQuery.get(url).done(function(htmlTemplate) {

    resolve({
      data : function() {
        return {}
      },
      props : [],
      computed : {},
      methods : {},
      mounted : function() {
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

const ItemsList = Vue.component('items-list', function(resolve, reject) {

  const url = 'views/itemsList.html'

  jQuery.get(url).done(function(htmlTemplate) {

    resolve({
      data : function() {
        return {
          form : {
            date : dateNowDayOnly(),
          },
          formResult : {},
          billed : {
            queries : {},
            items : [],
            pagination : {
              currentPageUi : 1,
              totalPages : 1,
              firstPage : true,
              lastPage : true,
            },
          },
          pending : {
            queries : {},
            items : [],
            pagination : {
              currentPageUi : 1,
              totalPages : 1,
              firstPage : true,
              lastPage : true,
            },
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
          c.refreshBilled(pageId)
          c.refreshPending(pageId)
        },
        refreshBilled : function(pageId) {
          if (pageId === undefined) {
            pageId = 1
          }
          var c = this

          c.billed.queries.pageId = pageId
          console.log('Items - Load Billed', c.billed.queries)
          httpGetQueries('/api/item/listBilled', c.billed.queries, function(data) {
            c.billed.pagination = data.pagination
            c.billed.items = data.items
            if (c.billed.items == null) {
              c.billed.items = []
            }
          })

        },
        refreshPending : function(pageId) {
          if (pageId === undefined) {
            pageId = 1
          }
          var c = this

          c.pending.queries.pageId = pageId
          console.log('Items - Load Pending', c.pending.queries)
          httpGetQueries('/api/item/listPending', c.pending.queries, function(data) {
            c.pending.pagination = data.pagination
            c.pending.items = data.items
            if (c.pending.items == null) {
              c.pending.items = []
            }
          })

        },
        create : function() {
          var c = this
          c.formResult = {}
          var clonedForm = JSON.parse(JSON.stringify(c.form))
          clonedForm.price = priceToLong(clonedForm.price)
          console.log('Item - Create', clonedForm)
          httpPost('/api/item', clonedForm, function(data) {
            c.formResult = data
            if (data.success) {
              jQuery('#createModal .btn-secondary').click()
              c.refreshPending(c.pending.queries.pageId)
            }
          })
        },
        billPending : function() {
          var c = this
          c.formResult = {}
          console.log('Item - Bill Pending', c.form)
          httpPost('/api/item/billPending', c.form, function(data) {
            c.formResult = data
            if (data.success) {
              jQuery('#billPendingModal .btn-secondary').click()
              c.refresh()
            }
          })
        },
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

const RecurrentItemsList = Vue.component('recurrent_items-list', function(resolve, reject) {

  const url = 'views/recurrentItemsList.html'

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

const ReportsList = Vue.component('reports-list', function(resolve, reject) {

  const url = 'views/reportsList.html'

  jQuery.get(url).done(function(htmlTemplate) {

    resolve({
      data : function() {
        return {
          reports : {},
        }
      },
      props : [],
      computed : {},
      methods : {
        refresh : function() {
          var c = this
          console.log('Reports - Load')
          httpGetQueries('/api/report', null, function(data) {
            c.reports = data.item
            if (c.reports == null) {
              c.reports = {}
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

const TechnicalSupportsList = Vue.component('technical_support-list', function(resolve, reject) {

  const url = 'views/technicalSupportsList.html'

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
