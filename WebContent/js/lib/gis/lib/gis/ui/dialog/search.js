/**
 * WKTを編集レイヤに表示するコントロール
 */
gis.ui.dialog.search = function(spec,my){
	my = my || {};

	var that = gis.ui.dialog(spec,my);

	/**
	 * コントロールのID
	 */
	my.id = spec.id || 'search';

	my.map = spec.map;

	/**
	 * コントロールの表示名
	 */
	my.label = spec.label || 'Search Data';

	my.tableId = "table-" + my.id;
	my.pagerId = "pager-" + my.id;

	my.selectedRow = null;

	my.marker = null;

	/**
	 * コンストラクタ
	 */
	my.init = function(){
		var html = my.getHtml();
		my.dialog.create(html,{
			title : my.label,
			modal : true,
			height : my.height,
			width : my.width,
			position : 'center',
			buttons : {
				'View' : my.btnView_onClick,
				'Close' : function(){
					$(this).dialog('close');
				}
			}
		},my.getData);
	};

	my.addOptions = function(option){
		option.title = my.label;
		option.modal = true,
		option.height = my.height,
		option.width = my.width
		option.position = { my: "center", at: "center", of: window },
		option.buttons = {
			'View' : my.btnView_onClick,
			'Close' : function(){
				$(this).dialog('close');
			}
		}
		return option;
	};

	my.postCreate = function(){
		my.getData();
	};

	my.getData = function(){
		$.ajax({
			url : my.url,
			type : 'GET',
			dataType : 'json',
			cache : false,
			async : false
    	}).done(function(json){
    		if (json.code !== 0){
    			alert(json.message);
    			return;
    		}
    		//テーブルの作成
           $("#" + my.tableId).jqGrid({
	           data:json.value, //表示したいデータ
	           datatype : "local", //データの種別 他にjsonやxmlも選べます。
	           //しかし、私はlocalが推奨です。
	           colNames : my.colNames, //列の表示名
	           colModel : my.colModelSettings, //列ごとの設定
	           rowNum : 10, //一ページに表示する行数
	           height : 270, //高さ
	           width : 910, //幅
	           pager : my.pagerId, //footerのページャー要素のid
	           viewrecords: true //footerの右下に表示する。
	           });
           $("#" + my.tableId).jqGrid('navGrid','#' + my.pagerId,{
        	   add:false, //おまじない
        	   edit:false, //おまじない
        	   del:false, //おまじない
        	   search:{ //検索オプション
        	   odata : ['equal', 'not equal', 'less', 'less or equal',
        	   'greater','greater or equal', 'begins with',
        	   'does not begin with','is in','is not in','ends with',
        	   'does not end with','contains','does not contain']
        	   } //検索の一致条件を入れられる
        	   });
         //filterバー追加
           $("#" + my.tableId).filterToolbar({
           defaultSearch:'cn' //一致条件を入れる。
           //選択肢['eq','ne','lt','le','gt','ge','bw','bn','in','ni','ew','en','cn','nc']
           });
    	}).fail(function(xhr){
			console.log(xhr.status + ';' + xhr.statusText);
			return false;
    	});
	};

	my.getHtml = function(){
		var html = "<table id='" + my.tableId + "'></table><div id = '" + my.pagerId + "'></div>";
		return html;
	};

	my.getPopupContent = function(data){
		return ""
	};

	my.createPopup = function(marker,data){
		return marker.bindPopup(my.getPopupContent(data)).openPopup();
	}

	my.btnView_onClick = function(){
		var selrows = $("#" + my.tableId).getGridParam('selrow');
		if (selrows.length === 0 || selrows.length > 1){
			alert("Please select a record.");
			return;
		}
		var row = $("#" + my.tableId).getRowData(selrows[0]);
		if (row.coordinates === ''){
			alert("Your selected record is not yet captured by GPS.")
			return;
		}
		//var layer = omnivore.wkt(row.wkt);
		var coordinates = row.coordinates.split(",")
		if (my.marker !== null){
			my.map.removeLayer(my.marker);
		}
		my.marker = L.marker(coordinates).addTo(my.map);
		my.map.setView(coordinates,18);
		my.createPopup(my.marker,row);
		that.close();
	};



	that.CLASS_NAME =  "gis.ui.dialog.search";
	return that;
};