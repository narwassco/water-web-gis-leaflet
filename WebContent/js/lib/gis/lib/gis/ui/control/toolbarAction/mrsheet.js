gis.ui.control.toolbarAction.mrsheet = function(spec,my){
	my = my || {};

	var that = gis.ui.control.toolbarAction(spec,my);

	my.id = spec.id ||'toolbarAction-mrsheet';
	my.html = spec.html ||'<img border="0" src="./js/lib/leaflet/custom-images/meter.png" width="25" height="25">';
	my.tooltip = spec.tooltip ||'Download Meter Reading Sheet';

	my.dialog = gis.ui.dialog.mrsheet({ divid : my.id });

	that.callback = function(){
		my.dialog.create({});
		my.dialog.open();
	};

	that.CLASS_NAME =  "gis.ui.control.toolbarAction.mrsheet";
	return that;
};