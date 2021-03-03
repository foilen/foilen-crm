const RecurrentItemsList = Vue.component('recurrent_items-list', function(resolve, reject) {

	const url = 'views/recurrentItemsList.html'

	jQuery.get(url).done(function(htmlTemplate) {

		resolve({
			data: function() {
				return {
					queries: {},
					items: [],
					frequencies: [
						[2, 'recurrence.monthly'],
						[1, 'recurrence.yearly'],
					],
					pagination: {
						currentPageUi: 1,
						totalPages: 1,
						firstPage: true,
						lastPage: true,
					},
					createForm: {
						nextGenerationDate: dateNowDayOnly(),
					},
					editForm: {
						clientShortName: null,
					},
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
					console.log('Recurrent Items - Load', c.queries)

					httpGetQueries('/api/recurrentItem/listAll', c.queries, function(data) {
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
					clonedForm.price = priceToLong(clonedForm.price)
					console.log('Recurrent Item - Create', clonedForm)

					httpPost('/api/recurrentItem', clonedForm, function(data) {
						c.formResult = data
						if (data.success) {
							jQuery('#createModal .btn-secondary').click()
							successShow(c.$t('prompt.create.success', [c.createForm.description]))
							c.refresh(c.queries.pageId)
						}
					})
				},
				deleteOne: function(item) {
					if (confirm(this.$t('prompt.delete.confirm', [item.id]))) {
						var c = this
						console.log('Recurrent Item - Delete', item.id)

						httpDelete('/api/recurrentItem/' + item.id, function() {
							successShow(c.$t('prompt.delete.success', [item.description]))
							c.refresh()
						})
					}
				},
				showEdit: function(item) {
					this.formResult = {}
					Object.assign(this.editForm, item)
					this.editForm.id = item.id
					this.editForm.nextGenerationDate = item.nextGenerationDateFormatted
					this.editForm.price = this.editForm.priceFormatted
					if (item.client) {
						this.editForm.clientShortName = item.client.shortName
					}
				},
				edit: function() {
					var c = this
					c.formResult = {}
					var clonedForm = JSON.parse(JSON.stringify(c.editForm))
					clonedForm.price = priceToLong(clonedForm.price)
					console.log('Recurrent Item - Edit', clonedForm)

					httpPut('/api/recurrentItem/' + clonedForm.id, clonedForm, function(data) {
						c.formResult = data
						if (data.success) {
							jQuery('#editModal .btn-secondary').click()
							successShow(c.$t('prompt.edit.success', [c.editForm.description]))
							c.refresh()
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
