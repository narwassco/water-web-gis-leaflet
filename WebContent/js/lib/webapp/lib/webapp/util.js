webapp.util = function(spec,my){
	my = my || {};
	var that= {};
	that.CLASS_NAME =  "webapp.util";
	return that;
};

webapp.util.ajaxGet = function(url,params, callback){
	var str_params = "";
	if (params){
		for (var name in params){
			if (str_params !== ""){
				str_params += "&";
			}
			str_params += name + "=" + params[name];
		}
		url = url + "?" + str_params;
	}

	$.ajax({
		url : url,
		type : "GET",
		async : false,
		dataType : "json",
		success : function(data){
			if (data.code !== 0){
				alert(data.message);
				return;
			}
			callback(data.value);
		},
		error : function(XMLHttpRequest, textStatus, errorThrown){
			  console.log(XMLHttpRequest, textStatus, errorThrown);
			}
	});
};