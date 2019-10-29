gis.ui.control.toolbarAction.print = function(spec,my){
	my = my || {};

	var that = gis.ui.control.toolbarAction(spec,my);

	/**
	 * コントロールのID
	 */
	my.id = spec.id || 'toolbarAction.print';

	my.html = spec.html || '<img border="0" src="./js/lib/leaflet/custom-images/print.png" width="25" height="25">';
	my.tooltip = spec.tooltip || 'Print Map';


	that.callback = function(){
		window.print();
	};


	that.CLASS_NAME =  "gis.ui.control.toolbarAction.print";
	return that;
};