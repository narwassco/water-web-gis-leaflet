/**
 * 地図編集コントロールを管理するスーパークラス
 */
gis.ui.control = function(spec,my){
	my = my || {};

	var that = gis.ui(spec,my);

	my.id = spec.id || undefined;
	my.map = spec.map || undefined;

	that.CLASS_NAME =  "gis.ui.control";
	return that;
};