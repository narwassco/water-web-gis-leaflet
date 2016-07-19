gis.ui.control.toolbarAction.differentVillage = function(spec,my){
	my = my || {};

	var that = gis.ui.control.toolbarAction(spec,my);

	/**
	 * コントロールのID
	 */
	my.id = spec.id || 'toolbarAction-differentVillage';

	my.html = spec.html || '<img border="0" src="./js/lib/leaflet/custom-images/village.png" width="25" height="25">';
	my.tooltip = spec.tooltip || 'Download Meter List of changing village';

	my.dialog = gis.ui.dialog.differentVillage({ divid : my.id });

	that.callback = function(){
		my.dialog.create({});
		my.dialog.open();
	};

	that.CLASS_NAME =  "gis.ui.control.toolbarAction.differentVillage";
	return that;
};