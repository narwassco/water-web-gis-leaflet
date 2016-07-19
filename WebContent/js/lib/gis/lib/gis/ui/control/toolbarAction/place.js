gis.ui.control.toolbarAction.place = function(spec,my){
	my = my || {};

	var that = gis.ui.control.toolbarAction(spec,my);

	/**
	 * コントロールのID
	 */
	my.id = spec.id || 'toolbarAction-place';

	my.html = spec.html || '<img border="0" src="./js/lib/leaflet/custom-images/place.png" width="25" height="25">';
	my.tooltip = spec.tooltip || 'Search Location of Place';

	my.dialog = gis.ui.dialog.search.placeView({ divid : my.id ,map : my.map});

	that.callback = function(){
		my.dialog.create({});
		my.dialog.open();
	};


	that.CLASS_NAME =  "gis.ui.control.toolbarAction.place";
	return that;
};