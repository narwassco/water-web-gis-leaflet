gis.ui.control.toolbarAction.customer = function(spec,my){
	my = my || {};

	var that = gis.ui.control.toolbarAction(spec,my);

	/**
	 * コントロールのID
	 */
	my.id = spec.id || 'toolbarAction-customer';

	my.html = spec.html || '<img border="0" src="./js/lib/leaflet/custom-images/customer.png" width="25" height="25">';
	my.tooltip = spec.tooltip || 'Search Location of Customer';

	my.dialog = gis.ui.dialog.search.customerView({ divid : my.id ,map : my.map});

	that.callback = function(){
		my.dialog.create({});
		my.dialog.open();
	};


	that.CLASS_NAME =  "gis.ui.control.toolbarAction.customer";
	return that;
};