gis.ui.dialog.adjustmentReport = function(spec,my){
	my = my || {};
	my.id = 'adjustmentReport';
	
	var that = gis.ui.dialog(spec,my);

	my.idymfrom = my.id + "-ymfrom";
	my.idymto = my.id + "-ymto";
	
	my.getHtml = function(){
		var now = new Date();
		var nowYear = now.getFullYear();
		var nowMonth = now.getMonth() + 1;
		var nowdate = "01/" + ('0' + nowMonth).slice(-2) + "/" + nowYear;
		
		var previous = new Date();
		previous.setDate(previous.getDate()-31);
		var preYear = previous.getFullYear();
		var preMonth = previous.getMonth() + 1;
		var predate = "01/" + ('0' + preMonth).slice(-2) + "/" + preYear;
		
		var html = "From:" + "<input type='text' id='" + my.idymfrom + "' value='" + predate + "' style='width:150px' readonly>" +
				" To:" + "<input type='text' id='" + my.idymto  + "' value='" + nowdate + "' style='width:150px' readonly>";

		return html;
	};

	my.addOptions = function(option){
		option.title = 'Download Consumption Report';
		option.width = 450,
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
		$("#" + my.idymfrom).datepicker({changeMonth:true,changeYear:true,dateFormat:'dd/mm/yy'});
		$("#" + my.idymto).datepicker({changeMonth:true,changeYear:true,dateFormat:'dd/mm/yy'});
	};

	my.download = function(){
		var fromdatestr = $("#" + my.idymfrom).val();
		var todatestr = $("#" + my.idymto).val();
		
		var fromdate = fromdatestr.substr(6,4) + fromdatestr.substr(3,2);
		var todate = todatestr.substr(6,4) + todatestr.substr(3,2);

		var intfromdate = Number(fromdate);
		var inttodate = Number(todate);
		
		if (intfromdate === inttodate){
			alert("From and To cannot be same.");
			return;
		}else if (intfromdate > inttodate){
			alert("Plese select From before To.");
			return;
		}
		
		$.ajax({
			url : './rest/BillingSync/AdjustmentReport?fromym=' + fromdate + '&toym=' + todate,
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


	that.CLASS_NAME =  "gis.ui.dialog.adjustmentReport";
	return that;
};