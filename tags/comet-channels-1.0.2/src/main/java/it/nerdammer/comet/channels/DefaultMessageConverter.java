package it.nerdammer.comet.channels;

import java.io.Reader;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.List;
import java.util.Map;

import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.type.TypeReference;

class DefaultMessageConverter implements MessageConverter {

	public String convertUnparsedJson(String unparsedJson, String channel) {
		ObjectMapper mapper = new ObjectMapper();

		MessageHandler hnd = null;
		try {
			Reader reader = new StringReader(unparsedJson);
			Map<String, Object> parsedJson = mapper.readValue(reader, new TypeReference<Map<String, Object>>() {});
			hnd = new MessageHandler(parsedJson, channel);
		} catch (Exception e) {
		}
		if(hnd==null) {
			try {
				Reader reader = new StringReader(unparsedJson);
				List<Object> parsedJson = mapper.readValue(reader, new TypeReference<List<Object>>() {});
				hnd = new MessageHandler(parsedJson, channel);
			} catch (Exception e) {	
			}
		}
		if(hnd==null) {
			try {
				Reader reader = new StringReader(unparsedJson);
				String parsedJson = mapper.readValue(reader, new TypeReference<String>() {});
				hnd = new MessageHandler(parsedJson, channel);
			} catch (Exception e) {
			}
		}
		if(hnd==null) {
			hnd = new MessageHandler(unparsedJson, channel);
		}
		
		String json = map(hnd);
		return json;
	}
	
	public String convert(Object jsonMessage, String channel) {
		if(jsonMessage==null) {
			throw new IllegalArgumentException("null jsonMessage");
		}
		
		MessageHandler hnd = new MessageHandler(jsonMessage, channel);
		String json = map(hnd);
		return json;
	}
	
	protected String map(MessageHandler handler) {
		ObjectMapper mapper = new ObjectMapper();
		StringWriter sw = new StringWriter();
		try {
			mapper.writeValue(sw, handler);
		} catch(Exception e) {
			throw new RuntimeException(e);
		}
		return sw.toString();
	}
	
}

class MessageHandler {

	private Object data;
	
	private String channel;
	
	public MessageHandler(Object data, String channel) {
		this.data = data;
		this.channel = channel;
	}
	
	public Object getData() {
		return data;
	}

	public String getChannel() {
		return channel;
	}
	
}

