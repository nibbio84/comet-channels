/*
 * Javascript code for the channel service
 */

/*
 * Read functions
 */

function initCometChannel(token, callback) {
	
	if(!token || token==null || token=="") {
		return;
	}
	
	
	cometChannelEstablishConnection(callback, token);
}

function cometChannelEstablishConnection(callback, token) {
	
	if(token==null) {
		return;
	}
	
	var url = "${contextPath}/comet-channels/channels";
	
	$.ajax({
		url: url,
		cache: false,
		type: "GET",
		data: {"token": token},
		dataType: "json",
		success: function(message) {

			if(message && message.data && message.channel) {
				if(typeof callback == 'function') {
					callback(message.data, message.channel);
				}
			}
		},
		complete: function(jqXHR, status) {
			if(status=="success") {
				cometChannelEstablishConnection(callback, token);
			} else {
				setTimeout(function() {
					cometChannelEstablishConnection(callback, token);
				}, 15000);
			}
		}
	});
	
}


/*
 * Write functions
 */

function cometChannelMessageSender(token) {
	
	this.token = token;
	
	this.send = function(message) {
		
		if(!message || message==null) {
			return;
		}
		
		var url = "${contextPath}/comet-channels/channels";
		
		$.ajax({
			url: url,
			cache: false,
			type: "POST",
			dataType: "json",
			data: {
					"token": token,
					"message": JSON.stringify(message)
				}
		});
		
	};
	
}






/*
 * Custom functions
 */
