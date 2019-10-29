gis.ui.control.toolbarAction.lolgorien = function(spec,my){
	my = my || {};

	var that = gis.ui.control.toolbarAction(spec,my);

	/**
	 * コントロールのID
	 */
	my.id = spec.id || 'toolbarAction.lolgorien';

	my.html = spec.html || 'Lolgorien';
	my.tooltip = spec.tooltip || 'zoom To Lolgorien';
	my.bounds = [[-1.22284266,34.79099915],[-1.2428443,34.82599266]];


	that.callback = function(){
		my.map.fitBounds(my.bounds);
    	my.map.zoomIn();
	};

	that.CLASS_NAME =  "gis.ui.control.toolbarAction.lolgorien";
	return that;
};