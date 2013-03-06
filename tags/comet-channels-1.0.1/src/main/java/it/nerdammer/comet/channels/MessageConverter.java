package it.nerdammer.comet.channels;

interface MessageConverter {

	public String convertUnparsedJson(String unparsedJson, String channel);
	
	public String convert(Object message, String channel);
	
}
