gis.ui.control.toolbarAction.kilgoris = function(spec,my){
	my = my || {};

	var that = gis.ui.control.toolbarAction(spec,my);

	/**
	 * コントロールのID
	 */
	my.id = spec.id || 'toolbarAction.kilgoris';

	my.html = spec.html || 'Kilgoris';
	my.tooltip = spec.tooltip || 'Zoom To Kilgoris';
	my.bounds = [[-0.99405233,34.85809698],[-1.01409134,34.8914455]];


	that.callback = function(){
		my.map.fitBounds(my.bounds);
    	my.map.zoomIn();
	};

	that.CLASS_NAME =  "gis.ui.control.toolbarAction.kilgoris";
	return that;
};