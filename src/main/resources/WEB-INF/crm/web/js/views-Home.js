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
