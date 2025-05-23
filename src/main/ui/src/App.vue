<template>
  <div class="container-fluid" id="app">
    <!-- errors -->
    <div class="row">
      <div id="errors"></div>
      <div id="successes"></div>
    </div>
    <!-- /errors -->

    <!-- Nav Bar -->
    <nav id="main-nav" class="navbar navbar-expand-sm navbar-light bg-light">
      <button class="navbar-toggler" type="button" data-toggle="collapse" data-target="#navbarToggler" aria-controls="navbarToggler" aria-expanded="false" aria-label="Toggle navigation">
        <span class="navbar-toggler-icon"></span>
      </button>
      <span class="navbar-brand mb-0 h1">Foilen CRM</span>

      <div class="collapse navbar-collapse" id="navbarToggler">
        <ul class="navbar-nav mr-auto">
          <li class="nav-item"><router-link class="nav-link" to="/" :class="{active: $route.path == '/'}">{{ $t('menu.home') }}</router-link></li>
          <li class="nav-item"><router-link class="nav-link" to="/clients" :class="{active: $route.path == '/clients'}">{{ $t('menu.clients') }}</router-link></li>
          <li class="nav-item"><router-link class="nav-link" to="/technicalSupports" :class="{active: $route.path == '/technicalSupports'}">{{ $t('menu.technicalSupports') }}</router-link></li>
          <li class="nav-item"><router-link class="nav-link" to="/items" :class="{active: $route.path == '/items'}">{{ $t('menu.items') }}</router-link></li>
          <li class="nav-item"><router-link class="nav-link" to="/transactions" :class="{active: $route.path == '/transactions'}">{{ $t('menu.transactions') }}</router-link></li>
          <li class="nav-item"><router-link class="nav-link" to="/recurrentItems" :class="{active: $route.path == '/recurrentItems'}">{{ $t('menu.recurrentItems') }}</router-link></li>
          <li class="nav-item"><router-link class="nav-link" to="/reports" :class="{active: $route.path == '/reports'}">{{ $t('menu.reports') }}</router-link></li>
        </ul>
        <ul class="navbar-nav">
          <li class="nav-item"><a class="nav-link" @click="changeLang">{{ $t('navbar.nextlang.name') }}</a></li>
        </ul>
      </div>
    </nav>
    <!-- /Nav Bar -->

    <!-- User details -->
    <div class="row">
      <div class="col-12" style="text-align: right">
        {{appDetails.userEmail}} <span v-if="appDetails.userAdmin"> (ADMIN) </span>
      </div>
    </div>
    <!-- /User details -->

    <!-- content -->
    <div>
      <router-view></router-view>
    </div>
    <!-- /content -->

    <!-- footer -->
    <hr />
    <footer>
      <p>
        &copy; <a href="https://foilen.com" target="_blank">Foilen</a> 2015-2025
      </p>
      <p>Version: {{appDetails.version}}</p>
    </footer>
  </div>
</template>

<script>
import axios from 'axios'

export default {
  name: 'App',
  data() {
    return {
      pendingAjax: 0,
      appDetails: {
        lang: 'en',
        userId: '',
        userEmail: '',
        userAdmin: false,
        version: '',
      },
    }
  },
  methods: {
    changeLang() {
      // Change
      const nextLang = this.$t('navbar.nextlang.id')
      this.$i18n.locale = nextLang

      // Persist
      axios.get('/index.html?lang=' + nextLang)
    },
    refresh() {
      axios.get('/api/app/details')
        .then(response => {
          this.appDetails = response.data.item
          this.$i18n.setLocaleMessage('en', this.appDetails.translations.en)
          this.$i18n.setLocaleMessage('fr', this.appDetails.translations.fr)
          this.$i18n.locale = this.appDetails.lang
        })
        .catch(error => {
          console.error('Error fetching app details:', error)
        })
    }
  },
  mounted() {
    this.refresh()
  }
}
</script>

<style>
/* Import your CSS here */
</style>