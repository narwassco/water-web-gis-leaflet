app = {

	map : null,
	dialog : {},

	init : function() {

		app.resize();

		$("#dialog").load("./dialog.html", function(){
			componentHandler.upgradeDom();

			app.dialog.login = webapp.ui.dialog.login({
				parentId:'header-layout',
				callback : function(){
					//$("#div-iframe").html('<iframe class="map" src="./map.html"></iframe>')
				}
			});
			app.dialog.login.init();
		});
	},

	resize : function() {
		var window_w = $(window).width();
		var window_h = $(window).height();
		var header_h = $(".mdl-layout__header").height();
		$(".contents").width(window_w);
		$(".contents").height(window_h - header_h);
	}
};

$(window).resize(function() {
	app.resize();
});

$(document).ready(function() {
	app.init();
});