gis.ui.control.toolbarAction.narok = function(spec,my){
	my = my || {};

	var that = gis.ui.control.toolbarAction(spec,my);

	/**
	 * コントロールのID
	 */
	my.id = spec.id || 'toolbarAction.narok';

	my.html = spec.html || 'Narok';
	my.tooltip = spec.tooltip || 'zoom To Narok';
	my.bounds = [[-1.11488791,35.84686198],[-1.05119559,35.89526577]];


	that.callback = function(){
		my.map.fitBounds(my.bounds);
    	my.map.zoomIn();
	};

	that.CLASS_NAME =  "gis.ui.control.toolbarAction.narok";
	return that;
};