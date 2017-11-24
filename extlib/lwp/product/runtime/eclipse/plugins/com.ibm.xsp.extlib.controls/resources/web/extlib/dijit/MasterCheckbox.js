"use strict"; // Turns bad habits into errors :)

dojo.provide("horizon.dijit.MasterCheckbox");

dojo.require("dijit._WidgetBase");
dojo.require("dojo.io.script");
dojo.require("dojo.NodeList-manipulate");
dojo.require("dojo.dom-class");

dojo.declare('horizon.dijit.MasterCheckbox', dijit._WidgetBase, {

	constructor : function(args) {

		// Merge the supplied arguments into this dijit object
		dojo.safeMixin(this, args);

		var context = this;

		// Install the On Click Toggle
		XSP.attachClientFunction(this.id + "_MASTER", 'onclick', function() {
			context.toggle();
		});

		var cbs = this.checkboxes();

		cbs.each(function() {

			var cbid = $(this).attr('id');
			var elem = $(this)[0];

			XSP.attachClientFunction(cbid, 'onchange', function(event) {

				context.updateMaster();
				context.updateParentStyle(elem);

			});

			context.updateParentStyle(elem);
			
			if (context.parentSelectorClick) {
				
				var parentClick = $(elem).closest(context.parentSelectorClick);
				
				parentClick.css('cursor', 'pointer');

				parentClick.click(function() {

					if ($(elem).prop('checked')) {
						$(elem).prop('checked', false);
					} else {
						$(elem).prop('checked', true);
					}

					context.updateParentStyle(elem);
					context.updateMaster();

				});
								
			}

		});

		context.updateMaster();

	},

	// elem = Checkbox Element
	updateParentStyle : function(elem) {

		if (this.parentSelectorStyleClass) {
		
			var parent = $(elem).closest(this.parentSelectorStyleClass);

			if ($(elem).prop('checked')) {
				parent.addClass(this.parentStyleClassChecked);
				parent.removeClass(this.parentStyleClassUnchecked);
			} else {
				parent.removeClass(this.parentStyleClassChecked);
				parent.addClass(this.parentStyleClassUnchecked);
			}
						
		}
		

	},

	updateMaster : function() {

		var cb = x$(this.masterId);
		var cbs = this.checkboxes();

		var somethingchecked = false;
		var somethingunchecked = false;

		cbs.each(function() {
			if ($(this).prop('checked')) {
				somethingchecked = true;
			} else {
				somethingunchecked = true;
			}
		});

		if (somethingunchecked && somethingchecked) {
			cb.prop('checked', false);
			cb.prop('indeterminate', true);
			//console.log('set indeterminate');
		} else if (somethingchecked) {
			cb.prop('indeterminate', false);
			cb.prop('checked', true);
			//console.log('set true');
		} else {
			cb.prop('indeterminate', false);
			cb.prop('checked', false);
			//console.log('set false');
		}

	},

	toggle : function() {

		var cbs = this.checkboxes();

		var somethingchecked = false;
		var somethingunchecked = false;

		cbs.each(function() {
			if ($(this).prop('checked')) {
				somethingchecked = true;
			} else {
				somethingunchecked = true;
			}
		});

		if (somethingunchecked) {
			cbs.prop('checked', true);
		} else if (somethingchecked) {
			cbs.prop('checked', false);
		}

		var context = this;

		cbs.each(function() {
			var elem = $(this)[0];
			context.updateParentStyle(elem);
		});

		this.updateMaster();

	},

	checkboxes : function() {
		return $('[name="' + this.id + '"]');
	},

	selectAll : function() {
		this.checkboxes().prop('checked', true);
	},

	deselectAll : function() {
		this.checkboxes().prop('checked', false);
	},

});