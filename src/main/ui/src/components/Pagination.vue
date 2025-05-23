<template>
  <div class="form-group">
    <button class="btn btn-small btn-primary" :disabled="pagination.firstPage" @click="previous">&lt;</button>
    {{pagination.currentPageUi}} / {{pagination.totalPages}}
    <button class="btn btn-small btn-primary" :disabled="pagination.lastPage" @click="next">&gt;</button>
  </div>
</template>

<script>
export default {
  name: 'Pagination',
  props: {
    pagination: {
      type: Object,
      default: () => ({
        currentPageUi: 1,
        totalPages: 1,
        firstPage: true,
        lastPage: true
      })
    }
  },
  methods: {
    previous() {
      this.$emit('changePage', {
        pageId: this.pagination.currentPageUi - 1
      })
    },
    next() {
      this.$emit('changePage', {
        pageId: this.pagination.currentPageUi + 1
      })
    }
  }
}
</script>

<style scoped>
.btn-small {
  padding: 0.25rem 0.5rem;
  font-size: 0.875rem;
  line-height: 1.5;
  border-radius: 0.2rem;
}
</style>