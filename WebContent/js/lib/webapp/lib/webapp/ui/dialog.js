webapp.ui.dialog = function(spec,my){
	my = my || {};
	var that = webapp.ui(spec,my);

	my.div = spec.div || '';
	my.dialog = null;

	my.parentId = spec.parentId || '';

	my.createMenu = function(){

	};

	my.registerEvents = function(){

	};

	that.init = function(){
		my.createMenu();

		my.dialog = document.getElementById(my.div);
	    if (! my.dialog.showModal) {
	      dialogPolyfill.registerDialog(my.dialog);
	    }

	    my.registerEvents();

	    $("#" + my.div + " .close").click(function() {
	    	that.close();
	    });
	};

	that.open = function(){
		my.dialog.showModal();
	};

	that.close = function(){
		my.dialog.close();
	};

	that.CLASS_NAME =  "webapp.ui.dialog";
	return that;
};