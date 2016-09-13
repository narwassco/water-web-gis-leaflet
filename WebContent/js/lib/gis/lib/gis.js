
(function() {

    /**
     * Before creating the OpenLayers namespace, check to see if
     * gis.singleFile is true.  This occurs if the
     * gis/SingleFile.js script is included before this one - as is the
     * case with old single file build profiles that included both
     * gis.js and justice/singleFile.js.
     */
    var singleFile = (typeof gis == "object" && gis.singleFile);

    /**
     * スクリプトのパス.
     */
    var scriptName = (!singleFile) ? "lib/gis.js" : "gis.js";

    /*
     * If window.justice isn't set when this script (justice.js) is
     * evaluated (and if singleFile is false) then this script will load
     * *all* justice scripts. If window.OpenLayers is set to an array
     * then this script will attempt to load scripts for each string of
     * the array, using the string as the src of the script.
     *
     * Example:
     * (code)
     *     <script type="text/javascript">
     *         window.gis = [
     *             "gis/util.js"
     *         ];
     *     </script>
     *     <script type="text/javascript" src="../lib/gis.js"></script>
     * (end)
     * In this example gis.js will load util.js only.
     */
    var jsFiles = window.gis;

    /**
     * 名前空間: gis
     * The gis object provides a namespace for all things gis
     */
    window.gis = {
    		/**
    		 * このスクリプトのパスを返す
    		 * @returns {function}
    		 */
            _getScriptLocation: (function() {
                var r = new RegExp("(^|(.*?\\/))(" + scriptName + ")(\\?|$)"),
                    s = document.getElementsByTagName('script'),
                    src, m, l = "";
                for(var i=0, len=s.length; i<len; i++) {
                    src = s[i].getAttribute('src');
                    if(src) {
                        m = src.match(r);
                        if(m) {
                            l = m[1];
                            break;
                        }
                    }
                }
                return (function() { return l; });
            })()
    };

    /**
     * OpenLayers.singleFile is a flag indicating this file is being included
     * in a Single File Library build of the OpenLayers Library.
     *
     * When we are *not* part of a SFL build we dynamically include the
     * OpenLayers library code.
     *
     * When we *are* part of a SFL build we do not dynamically include the
     * OpenLayers library code as it will be appended at the end of this file.
     */
    if(!singleFile) {
    	//読みこむスクリプトファイルパスを列挙する
        if (!jsFiles) {
            jsFiles = [
                'gis/ui.js',
                'gis/ui/control.js',
                'gis/ui/control/boxzoom.js',
                'gis/ui/control/measure.js',
                'gis/ui/control/mousePosition.js',
                'gis/ui/control/sidebar.js',
                'gis/ui/control/toolbarAction.js',
                'gis/ui/control/toolbarAction/adjustmentReport.js',
                'gis/ui/control/toolbarAction/billingUpload.js',
                'gis/ui/control/toolbarAction/consumptionReport.js',
                'gis/ui/control/toolbarAction/customer.js',
                'gis/ui/control/toolbarAction/differentVillage.js',
                'gis/ui/control/toolbarAction/mrsheet.js',
                'gis/ui/control/toolbarAction/narok.js',
                'gis/ui/control/toolbarAction/ololulunga.js',
                'gis/ui/control/toolbarAction/kilgoris.js',
                'gis/ui/control/toolbarAction/lolgorien.js',
                'gis/ui/control/toolbarAction/place.js',
                'gis/ui/control/toolbarAction/print.js',
                'gis/ui/control/toolbarAction/uncaptureByGps.js',
                'gis/ui/control/toolbarAction/worksheet.js',
                'gis/ui/control/toolbarAction/zoomToVillage.js',
                'gis/ui/dialog.js',
                'gis/ui/dialog/adjustmentReport.js',
                'gis/ui/dialog/billingUpload.js',
                'gis/ui/dialog/consumptionReport.js',
                'gis/ui/dialog/differentVillage.js',
                'gis/ui/dialog/login.js',
                'gis/ui/dialog/mrsheet.js',
                'gis/ui/dialog/search.js',
                'gis/ui/dialog/search/customerView.js',
                'gis/ui/dialog/search/placeView.js',
                'gis/ui/dialog/uncaptureByGps.js',
                'gis/ui/dialog/zoomToVillage.js',
                'gis/ui/toolbar.js',
                'gis/ui/layer.js'
            ]; // etc.
        }

      //スクリプトファイルを読み込む
        var scriptTags = new Array(jsFiles.length);
        var host = gis._getScriptLocation() + '/lib/';
        var date = new Date();
        for (var i=0, len=jsFiles.length; i<len; i++) {
        	var filepath = host + jsFiles[i] + "?version=" + date.getTime();
        	if (jsFiles[i].substr(0,1) === '.'){
        		filepath = jsFiles[i];
        	}
            scriptTags[i] = "<script src='" + filepath + "'></script>";
        }
        if (scriptTags.length > 0) {
            document.write(scriptTags.join(""));
        }
    }

})();

/**
 * Constant: VERSION_NUMBER
 */
gis.VERSION_NUMBER="Release 0.1";