package it.nerdammer.comet.channels;


interface TokenizedChannelService extends ChannelService {

	public String createReadToken(String channelID);
	
	public String createWriteToken(String channelID);
	
	public void addMessageListener(MessageListener listener);
	
	public void removeMessageListener(MessageListener listener);
	
	public void sendUnparsedMessage(String token, String unparsedMessage);
	
}
