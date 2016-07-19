this.gistools ={

		map : null,

		init:function(){
			this.map = L.map('map',{maxZoom:23}).setView([-1.08810653,35.85802695],13);
			this.setTools();
			L.control.scale().addTo(this.map);
		},

		setTools : function(){
			var objlayer = new gis.ui.layer({
				map : this.map,
				defineurl : './js/lib/gis/settings/define.json'
			}).init();

			gis.ui.control.mousePosition({map : this.map}).init();
			gis.ui.control.measure({map : this.map}).init();
			gis.ui.control.boxzoom({map : this.map}).init();

			var toolbar = gis.ui.toolbar({map : this.map});
			toolbar.init();

		}
}

$(document).ready(function(){
	gistools.init();
});