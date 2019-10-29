gis.ui.control.toolbarAction.billingUpload = function(spec,my){
	my = my || {};

	var that = gis.ui.control.toolbarAction(spec,my);

	/**
	 * コントロールのID
	 */
	my.id = spec.id || 'toolbarAction-billingUpload';

	my.html = spec.html || '<img border="0" src="./js/lib/leaflet/custom-images/upload.png" width="25" height="25">';
	my.tooltip = spec.tooltip || 'Upload Billing Data';

	my.dialog = gis.ui.dialog.billingUpload({ divid : my.id });

	that.callback = function(){
		my.dialog.create({});
		my.dialog.open();
	};

	that.CLASS_NAME =  "gis.ui.control.toolbarAction.billingUpload";
	return that;
};