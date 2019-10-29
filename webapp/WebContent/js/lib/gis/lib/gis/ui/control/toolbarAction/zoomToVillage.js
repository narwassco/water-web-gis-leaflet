gis.ui.control.toolbarAction.zoomToVillage = function(spec,my){
	my = my || {};

	var that = gis.ui.control.toolbarAction(spec,my);

	/**
	 * コントロールのID
	 */
	my.id = spec.id || 'toolbarAction-zoomToVillage';

	my.html = spec.html || '<img border="0" src="./js/lib/leaflet/custom-images/village.png" width="25" height="25">';
	my.tooltip = spec.tooltip || 'Zoom To Village';

	my.dialog = gis.ui.dialog.zoomToVillage({ divid : my.id, map : my.map });

	that.callback = function(){
		my.dialog.create({});
		my.dialog.open();
	};

	that.CLASS_NAME =  "gis.ui.control.toolbarAction.zoomToVillage";
	return that;
};