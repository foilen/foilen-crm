function refreshTypeahead() {
	jQuery('.typeahead').each(function(i, element) {
		var typeahead = jQuery(element)
		var parent = typeahead.parent()
		var list = jQuery('ul', typeahead)

		parent.focusin(function() {
			list.fadeIn()
		})
		parent.focusout(function() {
			list.fadeOut()
		})
	})
}

function dateNowDayOnly() {
	var d = new Date()
	return d.getFullYear() + '-' + ("0" + (d.getMonth() + 1)).slice(-2) + '-' + ("0" + d.getDate()).slice(-2)
}

function nullAllProperties(object) {
	Object.keys(object).forEach(k => object[k] = null)
}

function priceToLong(price) {
	return Math.round(parseFloat(price) * 100)
}
