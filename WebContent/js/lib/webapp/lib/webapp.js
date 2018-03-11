
(function() {

    /**
     * Before creating the OpenLayers namespace, check to see if
     * gis.singleFile is true.  This occurs if the
     * gis/SingleFile.js script is included before this one - as is the
     * case with old single file build profiles that included both
     * gis.js and justice/singleFile.js.
     */
    var singleFile = (typeof gis == "object" && gis.singleFile);

    var scriptName = (!singleFile) ? "lib/webapp.js" : "webapp.js";
    var jsFiles = window.webapp;

    window.webapp = {
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

    if(!singleFile) {
    	if (!jsFiles) {
            jsFiles = [
            	'webapp/util.js',
            	'webapp/ui.js',
                'webapp/ui/dialog.js',
                'webapp/ui/dialog/login.js'
            ]; // etc.
        }

    	var scriptTags = new Array(jsFiles.length);
        var host = webapp._getScriptLocation() + '/lib/';
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

webapp.VERSION_NUMBER="Release 0.1";