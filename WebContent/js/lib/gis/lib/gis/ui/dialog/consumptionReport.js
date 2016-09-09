gis.ui.dialog.consumptionReport = function(spec,my){
	my = my || {};
	my.id = 'consumptionReport';
	
	var that = gis.ui.dialog(spec,my);

	my.getHtml = function(){
		var now = new Date();
		var nowYear = now.getFullYear();

		var inserthtml = "<select id='" + my.id + "-month' style='width:40%'>";
		for (var i = 1; i <= 12; i++){
			inserthtml += "<option value='" + i + "'>" + i + "</option>";
		}
		inserthtml += "</select>";

		inserthtml += "<select id='" + my.id + "-year' style='width:60%'>";
		for (var i = nowYear; i > nowYear - 5; i--){
			inserthtml += "<option value='" + i + "'>" + i + "</option>";
		}
		inserthtml += "</select>";

		var html = "<table class='dialogstyle' style='width:100%'>" +
		"<tr><td>Month/Year</td><td>" + inserthtml + "</td></tr>" +
		"</table>";

		return html;
	};

	my.addOptions = function(option){
		option.title = 'Download Consumption Report';
		option.width = 400,
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

	my.postCreate = function(){
		var now = new Date();
		var nowYear = now.getFullYear();
		var nowMonth = now.getMonth() + 1;
		$("#" + my.id + "-year").val(nowYear);
		$("#" + my.id + "-month").val(nowMonth);
	};

	my.download = function(){
		var year = $("#" + my.id + "-year").val();
		var month = $("#" + my.id + "-month").val();

		$.ajax({
			url : './rest/BillingSync/ConsumptionReport?yearmonth=' + year + ("0" + month).slice(-2),
			type : 'GET',
			dataType : 'json',
			contentType : false,
			processData : false,
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


	that.CLASS_NAME =  "gis.ui.dialog.consumptionReport";
	return that;
};