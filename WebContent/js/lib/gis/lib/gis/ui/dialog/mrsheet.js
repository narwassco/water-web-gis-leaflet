gis.ui.dialog.mrsheet = function(spec,my){
	my = my || {};

	var that = gis.ui.dialog(spec,my);

	my.villages = {};

	my.areas = {};
	my.checkboxIdAndAreaMap = {};

	my.getHtml = function(){
		my.getVillages();
		var html = "";
		my.areas = {};
		for (var i = 0 in my.villages){
			var v = my.villages[i];
			if (!my.areas[v.area]){
				my.areas[v.area] = [];
			}
			my.areas[v.area].push(v);
		}

		html = "<ul>";
		for (var area in my.areas){
			html += "<li><label><input type='checkbox' id='checkAll" + area + "'>" + area + "</label></li>";
			html += "<ul id='checkboxArea" + area + "'>";
			for (var i = 0 in my.areas[area]){
				var v = my.areas[area][i];
				html += "<li><label><input type='checkbox' name='villages' value='" + v.villageid + "' checked>" + v.villageid + ":" + v.name + "</label></li>";
			}
			html += "</ul>";
		}
		html += "</ul>";

		return html;

	};

	my.postCreate = function(){
		my.setEventForCheckbox();
	};

	my.addOptions = function(option){
		option.title = 'Meter Reading Sheets';
		option.modal = true,
		option.height = 500,
		option.width = 500,
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

	my.setEventForCheckbox = function(){
		for (var area in my.areas){
			my.checkboxIdAndAreaMap["checkboxArea" + area] = area
			$("#checkboxArea" + area).click(function () {
				var _id = $(this).attr("id");
				var _area = my.checkboxIdAndAreaMap[_id];
				var checkboxCount = $("#" + _id + " input[type=checkbox]").length;
		        var selectedCount = $("#" + _id + " input[type=checkbox]:checked").length;
		        if (checkboxCount === selectedCount) {
		            $("#checkAll" + _area).prop("indeterminate", false).prop("checked", true );
		        } else if (0 === selectedCount) {
		            $("#checkAll" + _area).prop("indeterminate", false).prop("checked", false);
		        } else {
		            $("#checkAll" + _area).prop("indeterminate", true ).prop("checked", false);
		        }
			}).click();
			my.checkboxIdAndAreaMap["checkAll" + area] = area
			$("#checkAll" + area).click(function () {
				var _id = $(this).attr("id");
				var _area = my.checkboxIdAndAreaMap[_id];
				var checked = $("#" + _id).prop("checked");
				$("#checkboxArea" + _area + "  input[type=checkbox]").each(function(){
					$(this).prop("checked", checked);
				});
			});
		}
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
    		for (var i = 0 in villages){
    			var v = villages[i];
    			my.villages[v.villageid] = v;
    		}
    	}).fail(function(xhr){
			console.log(xhr.status + ';' + xhr.statusText);
			return false;
    	});
	}

	my.download = function(){
		var villages = [];
		$('[name="villages"]:checked').each(function(){
			villages.push($(this).val());
		});
		if (villages.length === 0){
			alert("Check a village at least.");
			return;
		}
		$.ajax({
			url : './rest/Meters/MReading?villageid=' + JSON.stringify(villages),
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

	that.CLASS_NAME =  "gis.ui.dialog.mrsheet";
	return that;
};