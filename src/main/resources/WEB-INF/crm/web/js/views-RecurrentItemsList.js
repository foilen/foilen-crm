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
