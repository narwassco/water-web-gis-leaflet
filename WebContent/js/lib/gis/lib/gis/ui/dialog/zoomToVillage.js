gis.ui.dialog.zoomToVillage = function(spec,my){
	my = my || {};

	var that = gis.ui.dialog(spec,my);

	my.map = spec.map;
	my.villages = null;
	my.isInit = false;
	my.comboboxId = 'cmbvillage_' + my.id;

	my.getHtml = function(){
		my.getVillages();
		var html = "<select id='" + my.comboboxId + "' style='width:100%'>";
		for (var i in my.villages){
			var v = my.villages[i];
			html += "<option value='" + v.villageid + "'>" + v.villageid + ":" + v.name + "</option>";
		}
		html += "</select>";
		return html;
	};

	my.addOptions = function(option){
		option.title = 'Zoom To Village';
		option.modal = true,
		option.position = { my: "center", at: "center", of: window },
		option.buttons = {
			'View' : my.btnZoomToVillage_onClick,
			'Close' : function(){
				that.close();
			}
		}
		return option;
	};

	my.getVillages = function(){
		$.ajax({
			url : './rest/Villages/',
			type : 'GET',
			dataType : 'json',
			cache : false,
			async : false
    	}).done(function(json){
    		if (json.code !== 0){
    			alert(json.message);
    			return;
    		}
    		var villages = json.value
    		my.villages = {};
    		for (var i in villages){
    			var v = villages[i];
    			if (v.wkt === null){
    				continue;
    			}
    			my.villages[v.villageid] = v;
    		}
    	}).fail(function(xhr){
			console.log(xhr.status + ';' + xhr.statusText);
			return false;
    	});
	}

	my.btnZoomToVillage_onClick = function(){
		var id = $("#" + my.comboboxId).val();
		var village = my.villages[id];
		var layer = omnivore.wkt.parse(village.wkt);
		my.map.fitBounds(layer.getBounds());
    	my.map.zoomIn();
		that.close();
	};

	that.CLASS_NAME =  "gis.ui.dialog.zoomToVillage";
	return that;
};