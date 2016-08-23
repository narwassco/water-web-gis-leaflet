gis.ui.toolbar = function(spec,my){
	my = my || {};

	var that = gis.ui(spec,my);

	my.map = spec.map || undefined;

	that.init =function(){
		var printAction = gis.ui.control.toolbarAction.print({map : gistools.map}).getAction();

		new L.Toolbar.Control({
            position: 'topleft',
            actions: [worksheetAction,printAction]
        }).addTo(my.map);
	};

	that.CLASS_NAME =  "gis.ui.toolbar";
	return that;
};