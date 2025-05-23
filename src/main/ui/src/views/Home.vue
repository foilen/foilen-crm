<template>
  <div class="row">
    <div class="col-12" v-if="$i18n.locale == 'en'">
      <h1>Welcome in Foilen CRM</h1>
      <p>
        This application is made to manage clients and invoices. It exposes this UI and also a REST API that you can
        view the <a href="/swagger-ui.html" target="_blank">documentation here</a>.
      </p>
    </div>

    <div class="col-12" v-if="$i18n.locale == 'fr'">
      <h1>Bienvenue dans Foilen CRM</h1>
      <p>
        Cette application sert à gérer des clients et des factures. Elle expose cet interface graphique ainsi qu'une API
        REST dont vous pouvez explorer <a href="/swagger-ui.html" target="_blank">la
        documentation ici</a>.
      </p>
    </div>
  </div>
</template>

<script>
export default {
  name: 'Home',
  data() {
    return {}
  },
  computed: {},
  methods: {},
  mounted() {
  }
}
</script>

<style scoped>
</style>