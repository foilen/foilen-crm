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
        createWithTime : function() {
          var c = this
          c.formResult = {}
          var clonedForm = JSON.parse(JSON.stringify(c.form))
          console.log('Item - Create With Time', clonedForm)
          httpPost('/api/item/createWithTime', clonedForm, function(data) {
            c.formResult = data
            if (data.success) {
              jQuery('#createWithTimeModal .btn-secondary').click()
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
        billSelected : function() {
          var c = this
          c.formResult = {}
          console.log('Item - Bill Selected', c.form)
          httpPost('/api/item/billSomePending', c.form, function(data) {
            c.formResult = data
            if (data.success) {
              jQuery('#billSelectedModal .btn-secondary').click()
              c.refresh()
            }
          })
        },
        showBillSelected : function() {
          var c = this
          c.form = {
            itemToBillIds : []
          }
          c.pending.items.forEach(function(element) {
            if (element.selected) {
              c.form.itemToBillIds.push(element.id)
            }
          })
          console.log(c.form.itemToBillIds)
          jQuery('#billSelectedModal').modal()
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
