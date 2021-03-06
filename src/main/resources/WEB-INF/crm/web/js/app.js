// Vue Router: https://router.vuejs.org
const routes = [ {
  path : '/',
  component : Home
}, {
  path : '/clients',
  component : ClientsList
}, {
  path : '/items',
  component : ItemsList
}, {
  path : '/recurrentItems',
  component : RecurrentItemsList
}, {
  path : '/reports',
  component : ReportsList
}, {
  path : '/technicalSupports',
  component : TechnicalSupportsList
}, {
  path : '/transactions',
  component : TransactionsList
}, ]

const router = new VueRouter({
  routes : routes
})

// Vue i18n
const messages = {}

const i18n = new VueI18n({
  locale : 'en',
  messages : messages,
})

// Main app
var app = new Vue({
  i18n : i18n,
  router : router,
  el : '#app',
  data : {
    pendingAjax : 0,
    appDetails : {
      lang : 'en',
      userId : '',
      userEmail : '',
      userAdmin : false,
      version : '',
    },
  },
  computed : {},
  methods : {
    changeLang : function() {
      // Change
      var nextLang = this.$t('navbar.nextlang.id')
      i18n.locale = nextLang

      // Persist
      jQuery.get('/index.html?lang=' + nextLang)
    },
    refresh : function() {
      httpGet('/api/app/details', function(data) {
        app.appDetails = data.item
        messages.en = app.appDetails.translations.en
        messages.fr = app.appDetails.translations.fr
        i18n.locale = app.appDetails.lang
      })
    }
  },
  mounted : function() {
    app = this
    this.refresh()
  }
})
