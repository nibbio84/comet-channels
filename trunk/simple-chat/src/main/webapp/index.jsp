<%@page contentType="text/html; charset=UTF-8" %>
<%@ taglib prefix="comet" uri="http://www.nerdammer.it/comet-channels" %>
<html>
<head>
	<title>Simple Chat</title>
	
	<link rel=stylesheet href="style.css" type="text/css">
		
	<script type="text/javascript" src="http://code.jquery.com/jquery-latest.js"></script>
	<comet:connect channel="chat" mode="rw" callback="onMessage" sender="messageSender" />
	
	<script type="text/javascript">
	
		function onMessage(message, channel) {
			$("#messaggi").append(message);
			$("#messaggi").append("<br/>");
			$("html, body").animate({ scrollTop: $(document).height() }, "slow");
		}
		
		$(document).ready(function() {
			
			$("#form").submit(function(ev) {
				ev.preventDefault();
				
				var txt = $("#testo").val();
				$("#testo").val("");
				if(txt!=null) {
					messageSender.send(txt);
				}
			});
			
		});
	
	</script>
</head>
<body>
<h1>Simple chat</h1>
<div id="messaggi"></div>
<div id="controlli">
	<form id="form">
		<div>
			<input type="text" id="testo" />
			<input type="submit" id="invia" value="Invia" />
		</div>
	</form>
</div>
</body>
</html>