package it.nerdammer.comet.channels;

import java.io.IOException;

interface MessageListener {

	public String getToken();
	
	public String getChannelID();
	
	public void registerChannelService(TokenizedChannelService service, String channelID);
	
	public void onMessage(String message) throws IOException;
	
}
