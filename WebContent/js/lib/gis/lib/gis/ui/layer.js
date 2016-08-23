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
				maxZoom:e.maxZoom,
				attribution: e.attribution,
			}).addTo(my.map);
		}else if (e.type === "TMS"){
			layer = L.tileLayer(e.url, {
			    tms: true,
			    crs: L.CRS.EPSG3857,
				maxZoom:e.maxZoom,
				attribution: e.attribution,
			}).addTo(my.map);
		}else if (e.type === "OSM"){
			layer = L.tileLayer('http://{s}.tile.osm.org/{z}/{x}/{y}.png', {
			    attribution: e.attribution,
				maxZoom:e.maxZoom
			}).addTo(my.map);
		}
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
					if (!obj.group){
						overlays[obj.name] = layer;
					}else{
						if (!overlays[obj.group]){
							overlays[obj.group] = {};
						}
						overlays[obj.group][obj.name] = layer
					}
				}

				if (obj.visible !== true){
					my.map.removeLayer(layer);
				}
			}

			var options = {
					  exclusiveGroups: ["Area"],
					  groupCheckboxes: true
					};
			L.control.groupedLayers(baseMaps,overlays,options).addTo(my.map);
		});
	};

	that.CLASS_NAME =  "gis.ui.layer";
	return that;
};