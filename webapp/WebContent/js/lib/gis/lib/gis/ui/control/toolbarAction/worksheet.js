gis.ui.control.toolbarAction.worksheet = function(spec,my){
	my = my || {};

	var that = gis.ui.control.toolbarAction(spec,my);

	/**
	 * コントロールのID
	 */
	my.id = spec.id || 'toolbarAction.worksheet';

	my.html = spec.html ||'<img border="0" src="./js/lib/leaflet/custom-images/worksheet.png" width="25" height="25">';
	my.tooltip = spec.tooltip || 'Export O&M Worksheet';

	that.callback = function(){
		var bbox = my.map.getBounds().getWest() + ',' + my.map.getBounds().getSouth() + ',' + my.map.getBounds().getEast() + ',' + my.map.getBounds().getNorth();
		$.ajax({
			url : './rest/MapPdf/OM?bbox=' + bbox,
			type : 'GET',
			dataType : 'json',
			cache : false,
			async : false
    	}).done(function(json){
    		if (json.code !== 0){
    			alert(json.message);
    			return;
    		}

    		window.open(json.value);
    	}).fail(function(xhr){
			console.log(xhr.status + ';' + xhr.statusText);
			return;
    	});
	};


	that.CLASS_NAME =  "gis.ui.control.toolbarAction.worksheet";
	return that;
};