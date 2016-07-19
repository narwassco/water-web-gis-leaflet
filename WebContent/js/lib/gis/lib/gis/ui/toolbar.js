/**
 * 地図編集コントロールを管理するスーパークラス
 */
gis.ui.toolbar = function(spec,my){
	my = my || {};

	var that = gis.ui(spec,my);

	my.map = spec.map || undefined;

	my.zoomactions = [
	                 gis.ui.control.toolbarAction.narok({map : my.map}).getAction(),
	                 gis.ui.control.toolbarAction.ololulunga({map : gistools.map}).getAction(),
	                 gis.ui.control.toolbarAction.kilgoris({map : gistools.map}).getAction(),
	                 gis.ui.control.toolbarAction.lolgorien({map : gistools.map}).getAction()
	                 ];

	my.billingactions = [
		                 gis.ui.control.toolbarAction.billingUpload({map : my.map}).getAction(),
		                 gis.ui.control.toolbarAction.mrsheet({map : gistools.map}).getAction(),
		                 gis.ui.control.toolbarAction.uncaptureByGps({map : gistools.map}).getAction(),
		                 gis.ui.control.toolbarAction.differentVillage({map : gistools.map}).getAction()
		                 ];

	my.placeactions = [
	                   	 gis.ui.control.toolbarAction.customer({map : my.map}).getAction(),
	                   	 gis.ui.control.toolbarAction.place({map : my.map}).getAction(),
	                   	gis.ui.control.toolbarAction.zoomToVillage({map : my.map}).getAction()
	                   ];

	that.init =function(){

		var searchMainActions = L.ToolbarAction.extend({
            options: {
                toolbarIcon: {
                	html:'<img border="0" src="./js/lib/leaflet/custom-images/search.png" width="25" height="25">',
                	tooltip:'Search Location'
                },
                subToolbar: new L.Toolbar({
                    actions: my.placeactions
                })
            }
        });

		var billingMainActions = L.ToolbarAction.extend({
            options: {
                toolbarIcon: {
                	html:'<img border="0" src="./js/lib/leaflet/custom-images/money.png" width="25" height="25">'
                },
                subToolbar: new L.Toolbar({
                    actions: my.billingactions
                })
            }
        });

		var zoomMainActions = L.ToolbarAction.extend({
            options: {
                toolbarIcon: {
                	html:'<img border="0" src="./js/lib/leaflet/custom-images/zoom-in.jpg" width="25" height="25">'
                },
                subToolbar: new L.Toolbar({
                    actions: my.zoomactions
                })
            }
        });

		var worksheetAction = gis.ui.control.toolbarAction.worksheet({map : gistools.map}).getAction();
		var printAction = gis.ui.control.toolbarAction.print({map : gistools.map}).getAction();

		new L.Toolbar.Control({
            position: 'topleft',
            actions: [zoomMainActions,searchMainActions,worksheetAction,billingMainActions,printAction]
        }).addTo(my.map);
	};

	that.CLASS_NAME =  "gis.ui.toolbar";
	return that;
};