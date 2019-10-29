gis.ui.control.toolbarAction.ololulunga = function(spec,my){
	my = my || {};

	var that = gis.ui.control.toolbarAction(spec,my);

	/**
	 * コントロールのID
	 */
	my.id = spec.id || 'toolbarAction.ololulunga';

	my.html = spec.html || 'Ololulunga';
	my.tooltip = spec.tooltip || 'Zoom To Ololulunga';
	my.bounds = [[-1.02494673,35.64209445],[-0.99425264,35.68120044]];

	that.callback = function(){
		my.map.fitBounds(my.bounds);
    	my.map.zoomIn();
	};

	that.CLASS_NAME =  "gis.ui.control.toolbarAction.ololulunga";
	return that;
};