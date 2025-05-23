<template>
  <div class="typeahead">
    <input type="text" class="form-control" :id="id" :value="value" @input="search" @focus="onFocus" @blur="onBlur"
           autocomplete="off">
    <ul v-if="focused">
      <li v-for="item in items" :key="item.sid" @click="selected(item.sid)">
        {{ item.sid }} ({{ item.pricePerHourFormatted }}$)
      </li>
    </ul>
  </div>
</template>

<script>
import http from '@/utils/http'

export default {
  name: 'TechnicalSupportSelect',
  props: {
    id: {
      type: String,
      default: 'technicalSupportSelect'
    },
    value: {
      type: String,
      default: ''
    }
  },
  data() {
    return {
      items: [],
      focused: false
    }
  },
  methods: {
    search(event) {
      let searchFor = null
      if (event) {
        searchFor = event.target.value
      }
      console.log('Search', searchFor)
      this.$emit('input', searchFor)

      http.get('/api/technicalSupport/listAll', {search: searchFor})
          .then(response => {
            this.items = response.data.items || []
            console.log('Technical Supports - Loaded', this.items.length, 'items for', searchFor)
          })
          .catch(error => {
            console.error('Error loading technical supports', error)
            this.items = []
          })
    },
    selected(sid) {
      console.log('Selected', sid)
      this.$emit('input', sid)
    },
    onFocus() {
      this.focused = true
    },
    onBlur() {
      // Small delay to allow click events on list items to complete before hiding the list
      setTimeout(() => {
        this.focused = false;
      }, 200)
    }
  },
  mounted() {
    this.search()
  }
}
</script>

<style scoped>
.typeahead {
  position: relative;
}

.typeahead ul {
  position: absolute;
  z-index: 1000;
  width: 100%;
  max-height: 300px;
  overflow-y: auto;
  padding: 0;
  margin: 0;
  list-style: none;
  background-color: #fff;
  border: 1px solid #ced4da;
  border-radius: 0.25rem;
}

.typeahead li {
  padding: 0.5rem;
  cursor: pointer;
}

.typeahead li:hover {
  background-color: #f8f9fa;
}
</style>
