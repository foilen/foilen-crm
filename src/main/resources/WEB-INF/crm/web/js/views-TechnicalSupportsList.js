const TechnicalSupportsList = Vue.component('technical_support-list', function(resolve, reject) {

	const url = 'views/technicalSupportsList.html'

	jQuery.get(url).done(function(htmlTemplate) {

		resolve({
			data: function() {
				return {
					queries: {},
					items: [],
					pagination: {
						currentPageUi: 1,
						totalPages: 1,
						firstPage: true,
						lastPage: true,
					},
					createForm: {},
					editForm: {},
					formResult: {},
				}
			},
			props: [],
			computed: {},
			methods: {
				refresh: function(pageId) {
					if (pageId === undefined) {
						pageId = 1
					}
					var c = this
					c.queries.pageId = pageId
					console.log('Technical Supports - Load', c.queries)

					httpGetQueries('/api/technicalSupport/listAll', c.queries, function(data) {
						c.pagination = data.pagination
						c.items = data.items
						if (c.items == null) {
							c.items = []
						}
					})
				},
				showCreate: function() {
					this.formResult = {}
				},
				create: function() {
					var c = this
					c.formResult = {}
					var clonedForm = JSON.parse(JSON.stringify(c.createForm))
					clonedForm.pricePerHour = priceToLong(clonedForm.pricePerHour)
					console.log('Technical Support - Create', clonedForm)

					httpPost('/api/technicalSupport', clonedForm, function(data) {
						c.formResult = data
						if (data.success) {
							jQuery('#createModal .btn-secondary').click()
							successShow(c.$t('prompt.create.success', [c.createForm.sid]))
							c.refresh(c.queries.pageId)
						}
					})
				},
				deleteOne: function(item) {
					if (confirm(this.$t('prompt.delete.confirm', [item.sid]))) {
						var c = this
						console.log('Technical Support - Delete', item.sid)

						httpDelete('/api/technicalSupport/' + item.sid, function() {
							successShow(c.$t('prompt.delete.success', [item.sid]))
							c.refresh(c.queries.pageId)
						})
					}
				},
				showEdit: function(item) {
					this.formResult = {}
					Object.assign(this.editForm, item)
					this.editForm.id = item.sid
					this.editForm.pricePerHour = this.editForm.pricePerHourFormatted
				},
				edit: function() {
					var c = this
					c.formResult = {}
					var clonedForm = JSON.parse(JSON.stringify(c.editForm))
					clonedForm.pricePerHour = priceToLong(clonedForm.pricePerHour)
					console.log('Technical Support - Edit', clonedForm)

					httpPut('/api/technicalSupport/' + clonedForm.id, clonedForm, function(data) {
						c.formResult = data
						if (data.success) {
							jQuery('#editModal .btn-secondary').click()
							successShow(c.$t('prompt.edit.success', [c.editForm.sid]))
							c.refresh(c.queries.pageId)
						}
					})
				},
			},
			mounted: function() {
				this.refresh()
			},
			template: htmlTemplate
		})

	}).fail(function(jqXHR, textStatus, errorThrown) {
		var escapedResponseText = new Option(jqXHR.responseText).innerHTML;
		var error = 'Could not get the template ' + url + ' . Error: ' + textStatus + ' ' + errorThrown + '<br/>' + escapedResponseText;
		errorShow(error)
		reject(error)
	})

})
