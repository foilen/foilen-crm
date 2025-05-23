<template>
  <div class="typeahead">
    <input type="text" class="form-control" :id="id" :value="value" @input="search" @focus="onFocus" @blur="onBlur" autocomplete="off">
    <ul v-if="focused">
      <li v-for="item in items" :key="item.shortName" @click="selected(item.shortName)">
        {{item.name}} ({{item.email}}) ({{item.lang}}) ({{item.shortName}})
      </li>
    </ul>
  </div>
</template>

<script>
import http from '@/utils/http'

export default {
  name: 'ClientSelect',
  props: {
    id: {
      type: String,
      default: 'clientSelect'
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

      http.get('/api/client/listAll', { search: searchFor })
        .then(response => {
          this.items = response.data.items || []
        })
        .catch(error => {
          console.error('Error loading clients', error)
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
