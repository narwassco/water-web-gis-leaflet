gis.ui.dialog.billingUpload = function(spec,my){
	my = my || {};

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
		"<tr><td colspan='2'><input type='file' id='" + my.id + "-file' style='width:100%'></td></tr>";

		return html;
	};

	my.addOptions = function(option){
		option.title = 'Upload Billing Data';
		option.width = 400,
		option.modal = true,
		option.position = { my: "center", at: "center", of: window },
		option.buttons = {
			'Upload' : function(){
				my.upload();
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

	my.upload = function(){
		var year = $("#" + my.id + "-year").val();
		var month = $("#" + my.id + "-month").val();
		var file = $("#" + my.id + "-file").val();

		if (file === ""){
			alert("Choose a csv file from Billing System which you want to upload.");
			return;
		}
		var fileobj = $("#" + my.id + "-file").prop('files')[0];
		var filename = fileobj.name;

		if (!confirm("Would you like to upload " + filename + " of " + month + " / " + year + " ?")){
			return;
		}

		var form = new FormData();
		form.append("file",fileobj);
		form.append("yearmonth",year + ("0" + month).slice(-2));

		$.ajax({
			url : './rest/BillingSync',
			data : form,
			type : 'POST',
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

    		alert("It succeeded to insert " + json.value + " records.");

    		that.close();
    	}).fail(function(xhr){
			console.log(xhr.status + ';' + xhr.statusText);
			return;
    	});
	};


	that.CLASS_NAME =  "gis.ui.dialog.billingUpload";
	return that;
};