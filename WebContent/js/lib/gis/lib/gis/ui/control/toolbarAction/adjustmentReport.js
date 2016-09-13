gis.ui.control.toolbarAction.adjustmentReport = function(spec,my){
	my = my || {};

	var that = gis.ui.control.toolbarAction(spec,my);

	/**
	 * コントロールのID
	 */
	my.id = spec.id || 'toolbarAction-adjustmentReport';

	my.html = spec.html || '<img border="0" src="./js/lib/leaflet/custom-images/adjustment.png" width="25" height="25">';
	my.tooltip = spec.tooltip || 'Download Adjustment Report';

	my.dialog = gis.ui.dialog.adjustmentReport({ divid : my.id });

	that.callback = function(){
		my.dialog.create({});
		my.dialog.open();
	};

	that.CLASS_NAME =  "gis.ui.control.toolbarAction.adjustmentReport";
	return that;
};