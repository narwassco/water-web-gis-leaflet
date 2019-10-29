gis.ui.dialog.login = function(spec,my){
	my = my || {};

	var that = gis.ui.dialog(spec,my);

	my.isSuccess = false;

	my.isInit = false;

	my.successCallback = null;


	my.getHtml = function(){
		var fields = [
		              {id : "password", label : "Password", type : "password", "class" : "validate[required]"}
		              ];

		var html = "<form id='form" + my.id + "' method='post'><table class='dialogstyle'>";
		for (var i = 0 in fields){
			var f = fields[i];
			html += "<tr><th style='width:40%'>" + f.label + "</th>";
			var option = "";
			if (f["class"]){
				option = "class='" + f["class"] + "'";
			}
			var insertHtml = "<input id='" + f.id + "' type='" + f.type + "' style='width:98%' " + option + "/>";
			html += "<td style='width:60%'>" + insertHtml + "</td>";
			html += "</tr>";
		}
		html += "</table></form>"
		return html;
	};

	my.addOptions = function(option){
		option.title = 'Login';
		option.modal = true,
		option.position = { my: "center", at: "center", of: window },
		option.buttons = {
			'Login' : my.btnLogin_onClick,
			'Cancel' : function(){
				that.close();
				my.successCallback(my.isSuccess);
			}
		}
		return option;
	};

	my.postCreate = function(){
		$("#form" + my.id).validationEngine('attach',{
			promptPosition:"inline"
		});
	};

	my.loginToServer = function(){
		$.ajax({
			url : './rest/Login?Password=' + $("#password").val(),
			type : 'GET',
			dataType : 'json',
			cache : false,
			async : false
    	}).done(function(json){
    		if (json.code !== 0){
    			alert(json.message);
    			return;
    		}
    		my.isSuccess = json.value;
    		if (my.isSuccess === false){
    			alert("Password is wrong. Please confirm password.");
    			$("#password").val("");
    			return;
    		}
    		my.successCallback(my.isSuccess);
    		that.close();
    	}).fail(function(xhr){
			console.log(xhr.status + ';' + xhr.statusText);
			return false;
    	});
	}

	my.btnLogin_onClick = function(){
		var valid = $("#form" + my.id).validationEngine('validate');
		if (valid !==true){
			return;
		}
		my.loginToServer();
	};

	that.login = function(successCallback){
		if (my.isSuccess === true){
			my.successCallback = successCallback;
			my.successCallback(my.isSuccess);
			return my.isSuccess;
		}

		that.create({});
		that.open();
		my.successCallback = successCallback;
	}

	that.CLASS_NAME =  "gis.ui.dialog.login";
	return that;
};