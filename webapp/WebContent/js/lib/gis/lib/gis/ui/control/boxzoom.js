gis.ui.control.boxzoom = function(spec,my){
	my = my || {};

	var that = gis.ui.control(spec,my);

	/**
	 * コントロールのID
	 */
	my.id = spec.id || 'boxzoom';

	my.control = null;

	that.init = function(){
		my.control = L.Control.boxzoom({ position:'topleft' }).addTo(my.map);
	};

	that.CLASS_NAME =  "gis.ui.control.boxzoom";
	return that;
};