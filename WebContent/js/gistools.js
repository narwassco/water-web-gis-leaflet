this.gistools ={

		map : null,

		init:function(){
			this.map = L.map('map',{
					maxZoom:23,
					fullscreenControl: true,
					fullscreenControlOptions: {position: 'topleft'},
					zoomsliderControl: true, 
					zoomControl: false, 
				}).setView([-0.3122,36.0526],13);
			this.setTools();
			L.control.scale().addTo(this.map);
		},

		setTools : function(){
			var objlayer = new gis.ui.layer({
				map : this.map,
				defineurl : './js/lib/gis/settings/define.json'
			}).init();
			
			var controls = ["mousePosition","measure","boxzoom","sidebar"];
			for (var i = 0 in controls){
				gis.ui.control[controls[i]]({map : this.map}).init();
			}
			
			var toolbar = gis.ui.toolbar({map : this.map});
			toolbar.init();
		}
}

$(document).ready(function(){
	gistools.init();
});