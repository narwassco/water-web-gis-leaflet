gis.ui.control.measure = function(spec,my){
	my = my || {};

	var that = gis.ui.control(spec,my);

	/**
	 * コントロールのID
	 */
	my.id = spec.id || 'measure';

	my.control = null;
	
	that.init = function(){
		my.control = new L.Control.Measure({
			position: 'topright',
			primaryLengthUnit: 'meters', secondaryLengthUnit: 'feet',
			primaryAreaUnit: 'sqmeters', secondaryAreaUnit: 'acres',
			activeColor: '#ABE67E',
			completedColor: '#C8F2BE',
			popupOptions: { className: 'leaflet-measure-resultpopup', autoPanPadding: [10, 10]},
			localization: 'en',
			decPoint: '.', thousandsSep: ','
		}).addTo(my.map);
	};

	that.CLASS_NAME =  "gis.ui.control.measure";
	return that;
};