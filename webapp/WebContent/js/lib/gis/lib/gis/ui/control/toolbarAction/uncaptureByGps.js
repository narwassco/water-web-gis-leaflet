gis.ui.control.toolbarAction.uncaptureByGps = function(spec,my){
	my = my || {};

	var that = gis.ui.control.toolbarAction(spec,my);

	/**
	 * コントロールのID
	 */
	my.id = spec.id || 'toolbarAction-uncaptureByGps';

	my.html = spec.html || '<img border="0" src="./js/lib/leaflet/custom-images/gps.png" width="25" height="25">';
	my.tooltip = spec.tooltip || 'Download Uncaptured Meters by GPS';

	my.dialog = gis.ui.dialog.uncaptureByGps({ divid : my.id });

	that.callback = function(){
		my.dialog.create({});
		my.dialog.open();
	};


	that.CLASS_NAME =  "gis.ui.control.toolbarAction.uncaptureByGps";
	return that;
};