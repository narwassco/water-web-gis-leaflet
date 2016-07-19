gis.ui.dialog.differentVillage = function(spec,my){
	my = my || {};

	var that = gis.ui.dialog(spec,my);

	my.zones = [{value:"A", display:"A(Narok)"},{value:"B", display:"B(Narok)"},{value:"C", display:"C(Ololulunga)"},{value:"D", display:"D(Kilgoris)"}];

	my.getHtml = function(){
		var html = "";
		for (var i = i in my.zones){
			var zone = my.zones[i];
			html += "<input type='checkbox' name='zone' value='" + zone.value + "' checked>" + zone.display + "<br>"
		}
		return html;
	};

	my.addOptions = function(option){
		option.title = 'List of Meters for Changing Villages';
		option.modal = true,
		option.position = { my: "center", at: "center", of: window },
		option.buttons = {
			'Download' : function(){
				my.download();
			},
			'Close' : function(){
				that.close();
			}
		}
		return option;
	};

	my.download = function(){
		var zones = [];
		$('[name="zone"]:checked').each(function(){
			zones.push($(this).val());
		});
		if (zones.length === 0){
			alert("Check a zone at least.");
			return;
		}
		$.ajax({
			url : './rest/Meters/VillageChange?zonecd=' + JSON.stringify(zones),
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
    		that.close();
    	}).fail(function(xhr){
			console.log(xhr.status + ';' + xhr.statusText);
			return;
    	});
	};

	that.CLASS_NAME =  "gis.ui.dialog.differentVillage";
	return that;
};