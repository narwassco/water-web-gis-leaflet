/**
 * uiコントロールの最上位クラス
 */
gis.ui.layer = function(spec,my){
	var that= {};

	my = my || {};

	my.map = spec.map;
	my.defineurl = spec.defineurl;

	my.getLayer = function(e){
		var layer = null;
		if (e.type === "WMS"){
			layer = L.tileLayer.wms(e.url,{
				layers:e.layers,
				format: 'image/png',
				transparent:true,
				crs: L.CRS.EPSG4326,
				maxZoom:e.maxZoom
			}).addTo(my.map);
		}else if (e.type === "TMS"){
			layer = L.tileLayer(e.url, {
			    tms: true,
			    crs: L.CRS.EPSG3857,
				maxZoom:e.maxZoom
			}).addTo(my.map);
		}else if (e.type === "OSM"){
			layer = L.tileLayer('http://{s}.tile.osm.org/{z}/{x}/{y}.png', {
			    attribution: '&copy; <a href="http://osm.org/copyright">OpenStreetMap</a> contributors',
				maxZoom:e.maxZoom
			}).addTo(my.map);
		}/*else if (e.type === "WFS"){
			layer = new L.WFS({
			    url: e.url,
			    geometryField: 'geom',
			    typeNS: '',
			    typeName: e.layers,
			    crs: L.CRS.EPSG4326,
			    style: {
			        color: 'blue',
			        weight: 2
			    }
			}).addTo(my.map);
		}*/
		return layer;
	};

	that.init = function(){
		$.ajax({
			url : my.defineurl,
			type : 'GET',
			dataType : 'json',
			cache : false,
			async : false
		}).done(function(layers_define) {
			var baseMaps = {};
			var overlays = {};

			for (var i = 0 in layers_define){
				var obj = layers_define[i];
				var layer = my.getLayer(obj);

				if (obj.isBaseLayer && obj.isBaseLayer === true){
					baseMaps[obj.name] = layer;
				}else{
					overlays[obj.name] = layer;
				}

				if (obj.visible !== true){
					my.map.removeLayer(layer);
				}
			}

			L.control.layers(baseMaps,overlays).addTo(my.map);
		});
	};

	that.CLASS_NAME =  "gis.ui.layer";
	return that;
};