package it.nerdammer.comet.channels;

import org.junit.Assert;
import org.junit.Test;

public class RobustnessTest {

	@Test
	public void setTwice() {
		TokenizedChannelService service = new DefaultChannelService(10);
		
		String token = service.createReadToken("canale1");
		
		MessageListenerMock lis1 = new MessageListenerMock(token);
		service.addMessageListener(lis1);
		try {
			service.addMessageListener(lis1);
			Assert.fail(); // Error
		} catch(IllegalArgumentException e) {
			// OK
		}
		
	}
	
	@Test
	public void removeNonExistent() {
		TokenizedChannelService service = new DefaultChannelService(10);
		
		String token = service.createReadToken("canale1");
		
		MessageListenerMock lis1 = new MessageListenerMock(token);
		try {
			service.removeMessageListener(lis1);
			Assert.fail(); // Error
		} catch(IllegalArgumentException e) {
			// OK
		}
		
	}
	
}
