gis.ui.control.toolbarAction.consumptionReport = function(spec,my){
	my = my || {};

	var that = gis.ui.control.toolbarAction(spec,my);

	/**
	 * コントロールのID
	 */
	my.id = spec.id || 'toolbarAction-consumptionReport';

	my.html = spec.html || '<img border="0" src="./js/lib/leaflet/custom-images/water_consumption.png" width="25" height="25">';
	my.tooltip = spec.tooltip || 'Download Monthly Consumption Data by Villages';

	my.dialog = gis.ui.dialog.consumptionReport({ divid : my.id });

	that.callback = function(){
		my.dialog.create({});
		my.dialog.open();
	};

	that.CLASS_NAME =  "gis.ui.control.toolbarAction.consumptionReport";
	return that;
};