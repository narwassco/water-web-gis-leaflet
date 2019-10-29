/**
 * WKTを編集レイヤに表示するコントロール
 */
gis.ui.dialog.search.placeView = function(spec,my){
	my = my || {};

	var that = gis.ui.dialog.search(spec,my);

	my.id = spec.id || 'placeView';
	my.label = spec.label || 'Search Place';
	my.height = 510;
	my.width = 930;
	my.tableId = "table-" + my.id;
	my.pagerId = "pager-" + my.id;
	my.url = './rest/Places/';
	my.colModelSettings= [
       {name:"placeid",index:"placeid",width:50,align:"right",classes:"placeid_class"},
       {name:"name",index:"name",width:200,align:"left",classes:"name_class"},
       {name:"category",index:"category",width:150,align:"left",classes:"category_class"},
       {name:"coordinates",index:"coordinates",width:150,align:"left",classes:"coordinates_class"}
   ]
	my.colNames = ["Place ID","Place Name","Category","Location"];

	my.getPopupContent = function(data){
		return data.name;
	};

	that.CLASS_NAME =  "gis.ui.dialog.search.customerView";
	return that;
};