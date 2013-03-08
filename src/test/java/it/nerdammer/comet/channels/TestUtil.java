package it.nerdammer.comet.channels;

import java.util.Map;

import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.type.TypeReference;

public class TestUtil {

	public static String unwrapJsonString(String json) {
		try {
			ObjectMapper mapper = new ObjectMapper();
			Map<String, Object> hnd = mapper.readValue(json, new TypeReference<Map<String, Object>>() {});
			Object res = hnd.get("data");
			return (String) res;
		} catch(Exception e) {
			throw new RuntimeException(e);
		}
	}
	
}
