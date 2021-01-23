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
