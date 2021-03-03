const ItemsList = Vue.component('items-list', function(resolve, reject) {

	const url = 'views/itemsList.html'

	jQuery.get(url).done(function(htmlTemplate) {

		resolve({
			data: function() {
				return {
					billed: {
						queries: {},
						items: [],
						pagination: {
							currentPageUi: 1,
							totalPages: 1,
							firstPage: true,
							lastPage: true,
						},
					},
					pending: {
						queries: {},
						items: [],
						pagination: {
							currentPageUi: 1,
							totalPages: 1,
							firstPage: true,
							lastPage: true,
						},
					},
					billForm: {},
					createForm: {
						date: dateNowDayOnly(),
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
					c.refreshBilled(pageId)
					c.refreshPending(pageId)
				},
				refreshBilled: function(pageId) {
					if (pageId === undefined) {
						pageId = 1
					}
					var c = this

					c.billed.queries.pageId = pageId
					console.log('Items - Load Billed', c.billed.queries)
					httpGetQueries('/api/item/listBilled', c.billed.queries, function(data) {
						c.billed.pagination = data.pagination
						c.billed.items = data.items
						if (c.billed.items == null) {
							c.billed.items = []
						}
					})

				},
				refreshPending: function(pageId) {
					if (pageId === undefined) {
						pageId = 1
					}
					var c = this

					c.pending.queries.pageId = pageId
					console.log('Items - Load Pending', c.pending.queries)
					httpGetQueries('/api/item/listPending', c.pending.queries, function(data) {
						c.pending.pagination = data.pagination
						c.pending.items = data.items
						if (c.pending.items == null) {
							c.pending.items = []
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
					console.log('Item - Create', clonedForm)

					httpPost('/api/item', clonedForm, function(data) {
						c.formResult = data
						if (data.success) {
							jQuery('#createModal .btn-secondary').click()
							successShow(c.$t('prompt.create.success', [c.createForm.description]))
							c.refreshPending(c.pending.queries.pageId)
						}
					})
				},
				createWithTime: function() {
					var c = this
					c.formResult = {}
					var clonedForm = JSON.parse(JSON.stringify(c.createForm))
					console.log('Item - Create With Time', clonedForm)

					httpPost('/api/item/createWithTime', clonedForm, function(data) {
						c.formResult = data
						if (data.success) {
							jQuery('#createWithTimeModal .btn-secondary').click()
							c.refreshPending(c.pending.queries.pageId)
						}
					})
				},
				billPending: function() {
					var c = this
					c.formResult = {}
					console.log('Item - Bill Pending', c.billForm)

					httpPost('/api/item/billPending', c.billForm, function(data) {
						c.formResult = data
						if (data.success) {
							jQuery('#billPendingModal .btn-secondary').click()
							c.refresh()
						}
					})
				},
				billSelected: function() {
					var c = this
					c.formResult = {}
					console.log('Item - Bill Selected', c.billForm)

					httpPost('/api/item/billSomePending', c.billForm, function(data) {
						c.formResult = data
						if (data.success) {
							jQuery('#billSelectedModal .btn-secondary').click()
							c.refresh()
						}
					})
				},
				deleteOne: function(item) {
					if (confirm(this.$t('prompt.delete.confirm', [item.id]))) {
						var c = this
						console.log('Item - Pending - Delete', item.id)

						httpDelete('/api/item/' + item.id, function() {
							successShow(c.$t('prompt.delete.success', [item.id]))
							c.refresh()
						})
					}
				},
				showBillSelected: function() {
					var c = this
					c.formResult = {}
					c.billForm = {
						itemToBillIds: []
					}
					c.pending.items.forEach(function(element) {
						if (element.selected) {
							c.billForm.itemToBillIds.push(element.id)
						}
					})
					console.log(c.billForm.itemToBillIds)
					jQuery('#billSelectedModal').modal()
				},
				showEdit: function(item) {
					this.formResult = {}
					Object.assign(this.editForm, item)
					this.editForm.id = item.id
					this.editForm.price = this.editForm.priceFormatted
					this.editForm.date = this.editForm.dateFormatted
					if (item.client) {
						this.editForm.clientShortName = item.client.shortName
					}
				},
				edit: function() {
					var c = this
					c.formResult = {}
					var clonedForm = JSON.parse(JSON.stringify(c.editForm))
					clonedForm.price = priceToLong(clonedForm.price)
					console.log('Item - Pending - Edit', clonedForm)

					httpPut('/api/item/' + c.editForm.id, clonedForm, function(data) {
						c.formResult = data
						if (data.success) {
							jQuery('#editModal .btn-secondary').click()
							successShow(c.$t('prompt.edit.success', [c.editForm.id]))
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
