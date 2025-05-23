<template>
  <div class="row">
    <div class="col-12">
      <p class="background-danger" v-if="formResult.error">{{formResult.error.timestamp}} {{formResult.error.uniqueId}} {{formResult.error.message}}</p>
      <ul v-if="formResult.globalErrors">
        <li class="bg-danger" v-for="item in formResult.globalErrors" :key="item">{{item}}</li>
      </ul>
      <ul v-if="formResult.globalWarnings">
        <li class="bg-warning" v-for="item in formResult.globalWarnings" :key="item">{{item}}</li>
      </ul>
    </div>
  </div>
</template>

<script>
export default {
  name: 'ErrorResults',
  props: {
    formResult: {
      type: Object,
      default: () => ({})
    }
  }
}
</script>

<style scoped>
.bg-danger {
  background-color: #f8d7da;
  color: #721c24;
  padding: 0.5rem;
  border-radius: 0.25rem;
}

.bg-warning {
  background-color: #fff3cd;
  color: #856404;
  padding: 0.5rem;
  border-radius: 0.25rem;
}

.background-danger {
  background-color: #f8d7da;
  color: #721c24;
  padding: 0.5rem;
  border-radius: 0.25rem;
}
</style>