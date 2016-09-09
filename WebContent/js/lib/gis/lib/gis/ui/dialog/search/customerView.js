/**
 * WKTを編集レイヤに表示するコントロール
 */
gis.ui.dialog.search.customerView = function(spec,my){
	my = my || {};

	var that = gis.ui.dialog.search(spec,my);

	my.id = spec.id || 'customerView';
	my.label = spec.label || 'Search Customer';
	my.height = 510;
	my.width = 940;
	my.tableId = "table-" + my.id;
	my.pagerId = "pager-" + my.id;
	my.url = './rest/Customers/';
	my.colModelSettings= [
       {name:"villageid",index:"villageid",width:60,align:"center",classes:"villageid_class"},
       {name:"villagename",index:"villagename",width:150,align:"left",classes:"villagename_class"},
       {name:"zone",index:"zone",width:50,align:"center",classes:"zone_class"},
       {name:"con",index:"con",width:70,align:"left",classes:"con_class"},
       {name:"name",index:"name",width:300,align:"left",classes:"name_class"},
       {name:"status",index:"status",width:60,align:"center",classes:"status_class"},
       {name:"serialno",index:"serialno",width:150,align:"left",classes:"serialno_class"},
       {name:"coordinates",index:"coordinates",width:300,align:"left",classes:"coordinates_class"}
   ]
	my.colNames = ["Village ID","Village Name","Zone","Con","Customer Name","Status","Meter S/N","Location"];

	my.getPopupContent = function(data){
		return data.con + data.zone + "<br>" + data.name;
	};
	
	my.getButtons = function(){
		var buttons = {
				'Statement' : my.btnStatement_onClick,
				'View' : my.btnView_onClick,
				'Close' : function(){
					that.close();
				}
		}
		return buttons;
	};
	
	my.btnStatement_onClick = function(){
		var selrows = $("#" + my.tableId).getGridParam('selrow');
		if (selrows.length === 0 || selrows.length > 1){
			alert("Please select a record.");
			return;
		}
		var row = $("#" + my.tableId).getRowData(selrows[0]);
		
		$.ajax({
			url : './rest/BillingSync/Statement?zone=' + row.zone + '&connectionno=' + row.con,
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

	that.CLASS_NAME =  "gis.ui.dialog.search.customerView";
	return that;
};