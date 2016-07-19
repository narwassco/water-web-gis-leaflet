/**
 * uiコントロールの最上位クラス
 */
gis.ui = function(spec,my){
	var that= {};

	my = my || {};

	/**
	 * UIコントロールを表示するDIVタグID
	 */
	my.divid = spec.divid;

	that.getHeight = function(){
		return $("#" + my.divid).height();
	};

	that.CLASS_NAME =  "gis.ui";
	return that;
};