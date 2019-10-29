gis.ui.control.toolbarAction = function(spec,my){
	my = my || {};

	var that = gis.ui.control(spec,my);

	/**
	 * コントロールのID
	 */
	my.id = spec.id;
	my.html = spec.html;
	my.tooltip = spec.tooltip;

	my.loginDialog = spec.loginDialog || null;

	my.ImmediateSubAction = null;

	my.Action = null;

	that.init = function(){
		my.ImmediateSubAction = L.ToolbarAction.extend({
	        initialize: function(map, myAction) {
	            this.map = map;
	            this.myAction = myAction;
	            L.ToolbarAction.prototype.initialize.call(this);
	        },
	        addHooks: function() {
	            this.myAction.disable();
	        }
	    });

		my.Action = my.ImmediateSubAction.extend({
			options: {
	            toolbarIcon: {
	                html: my.html,
	                tooltip: my.tooltip
	            }
	        },
	        addHooks: function () {

	        	if (my.loginDialog !== null){
	        		my.loginDialog.login(function(isSuccess){
	        			if (isSuccess === true){
	        				that.callback();
	        			}
	        		});
	        	}else{
	        		that.callback();
	        	}
	        	if (this.myAction !== undefined){
	        		this.myAction.disable();
	        	}
	        }
	    });
	};

	that.callback = function(){
		return;
	};

	that.getAction = function(){
		that.init();
		return my.Action;
	};

	that.CLASS_NAME =  "gis.ui.control.toolbarAction";
	return that;
};