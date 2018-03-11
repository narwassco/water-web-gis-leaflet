webapp.ui.dialog.login = function(spec,my){
	my = my || {};
	var that = webapp.ui.dialog(spec,my);

	my.div = 'login-dialog';
	my.account = null;

	my.callback = spec.callback || null;

	my.menuId = 'dialog-login-menu-button-login';
	my.menuLabel = 'dialog-login-menu-label-login';

	my.createMenu = function(){
		if (my.parentId === ''){
			return;
		}
		var html = "<label id='" + my.menuLabel + "'></label><button class='mdl-button mdl-js-button mdl-button--raised mdl-js-ripple-effect' id='" + my.menuId + "'></button>"
		$("#" + my.parentId).append(html);
		my.changeMenuName();
		componentHandler.upgradeDom();

		$('#' + my.menuId).click(function() {
			if (my.account === null){
				that.open();
			}else{
				that.logout();
			}
	    });
	};

	my.changeMenuName = function(){
		if (my.account === null){
			$("#" + my.menuId).text("LOGIN");
			$("#" + my.menuLabel).text("");
		}else{
			$("#" + my.menuId).text("LOGOUT");
			$("#" + my.menuLabel).text(my.account.loginid);
		}
	};

	my.registerEvents = function(){
		$("#" + my.div + " .login-button").click(function() {
			var account = $("#" + my.div + " .account-text").val();
			var password = $("#" + my.div + " .password-text").val();

			if (account == "" || password == ""){
				return;
			};
			//Login
			var params = {id : account, password : password};
			webapp.util.ajaxGet("./rest/Token/Login",params,function(value){
				my.account = value;
				my.changeMenuName();
				that.close();

				if (my.callback){
					my.callback();
				}
			});
		});
	};

	that.getToken = function(){
		return my.account.token;
	};

	that.logout = function(){
		var params = {token : my.account.token};
		webapp.util.ajaxGet("./rest/Token/Logout",params,function(value){
			my.account = null;
			my.changeMenuName();
			that.close();
		});
	}

	that.CLASS_NAME =  "webapp.ui.dialog.login";
	return that;
};