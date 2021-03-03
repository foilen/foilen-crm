const ClientsList = Vue.component('clients-list', function(resolve, reject) {

	const url = 'views/clientsList.html'

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
					editForm: {
						clientShortName: null,
						technicalSupportSid: null,
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
					console.log('Clients - Load', c.queries)

					httpGetQueries('/api/client/listAll', c.queries, function(data) {
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
					console.log('Clients - Create', c.createForm)

					httpPost('/api/client', c.createForm, function(data) {
						c.formResult = data
						if (data.success) {
							jQuery('#createModal .btn-secondary').click()
							successShow(c.$t('prompt.create.success', [c.createForm.shortName]))
							c.refresh(c.queries.pageId)
						}
					})
				},
				deleteOne: function(item) {
					if (confirm(this.$t('prompt.delete.confirm', [item.name]))) {
						var c = this
						console.log('Client - Delete', item.name)

						httpDelete('/api/client/' + item.shortName, function() {
							successShow(c.$t('prompt.delete.success', [item.shortName]))
							c.refresh(c.queries.pageId)
						})
					}
				},
				showEdit: function(item) {
					this.formResult = {}
					Object.assign(this.editForm, item)
					this.editForm.clientShortName = item.shortName
					if (item.technicalSupport) {
						this.editForm.technicalSupportSid = item.technicalSupport.sid
					}
				},
				edit: function() {
					var c = this
					c.formResult = {}
					console.log('Clients - Edit', c.editForm)

					httpPut('/api/client/' + c.editForm.clientShortName, c.editForm, function(data) {
						c.formResult = data
						if (data.success) {
							jQuery('#editModal .btn-secondary').click()
							successShow(c.$t('prompt.edit.success', [c.editForm.name]))
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
