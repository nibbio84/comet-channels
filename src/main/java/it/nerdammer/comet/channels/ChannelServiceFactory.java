package it.nerdammer.comet.channels;

public final class ChannelServiceFactory {

	private static final ChannelService service = new DefaultChannelService(2);
	
	private static final MessageConverter converter = new DefaultMessageConverter();
	
	private ChannelServiceFactory() {
	}
	
	public static ChannelService getChannelService() {
		return service;
	}
	
	protected static MessageConverter getMessageConverter() {
		return converter;
	}
	
}
