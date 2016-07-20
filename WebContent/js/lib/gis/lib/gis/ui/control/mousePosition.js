/**
 * マウスの表示位置座標を取得するコントロール
 */
gis.ui.control.mousePosition = function(spec,my){
	my = my || {};

	var that = gis.ui.control(spec,my);

	/**
	 * コントロールのID
	 */
	my.id = spec.id || 'mousePosition';

	that.init = function(){
		L.control.coordinates({
		    position:"bottomright", //optional default "bootomright"
		    decimals:6, //optional default 4
		    decimalSeperator:".", //optional default "."
		    labelTemplateLat:"Latitude: {y}", //optional default "Lat: {y}"
		    labelTemplateLng:"Longitude: {x}", //optional default "Lng: {x}"
		    enableUserInput:false, //optional default true
		    useDMS:false, //optional default false
		    useLatLngOrder: false, //ordering of labels, default false-> lng-lat
		    markerType: L.marker, //optional default L.marker
		    markerProps: {}, //optional default {},
		    //labelFormatterLng : function(lng){return lng+" lng"}, //optional default none,
		    //labelFormatterLat : function(lat){return lat+" lat"}, //optional default none,
		    //customLabelFcn: function(latLonObj, opts) { *"Geohash: " + encodeGeoHash(latLonObj.lat, latLonObj.lng)} //optional default none
		}).addTo(my.map);
	};

	that.CLASS_NAME =  "gis.ui.control.mousePosition";
	return that;
};