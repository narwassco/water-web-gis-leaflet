gis.ui.control.sidebar = function(spec,my){
	my = my || {};

	var that = gis.ui.control(spec,my);

	my.divid = spec.divid || 'sidebar';
	my.url = spec.url || './js/lib/gis/settings/sidebar.html';
	
	my.control = null;

	that.init = function(){
		$.ajax({
			url : my.url,
			type : 'GET',
			dataType : 'html',
			cache : true,
			async : false
    	}).done(function(html){
    		$("#map").before(html);
    		$("#map").addClass("sidebar-map");
    	}).fail(function(xhr){
			console.log(xhr.status + ';' + xhr.statusText);
			return false;
    	});
		
		my.control = L.control.sidebar(my.divid).addTo(my.map);
	};

	that.CLASS_NAME =  "gis.ui.control.sidebar";
	return that;
};