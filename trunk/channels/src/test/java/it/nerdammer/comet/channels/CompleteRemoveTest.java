package it.nerdammer.comet.channels;

import org.junit.Assert;
import org.junit.Test;

public class CompleteRemoveTest {

	
	@Test
	public void testNoRedelivery() throws InterruptedException {
		
		DefaultChannelService service = new DefaultChannelService(10);
		service.setCleanupTime(150);
		
		String token = service.createReadToken("canale1");
		
		MessageListenerMock lis1 = new MessageListenerMock(token);
		service.addMessageListener(lis1);
		
		service.sendMessage("ciccia", "canale1");
		
		String wrapped = lis1.waitForMessage();
		String message = TestUtil.unwrapJsonString(wrapped);

		Assert.assertEquals("ciccia", message);
		
		service.sendMessage("ciccia2", "canale1");
		Thread.sleep(170);
		
		MessageListenerMock lis2 = new MessageListenerMock(token);
		service.addMessageListener(lis2);
		
		try {
			lis2.waitForMessage(100);
			Assert.fail();
		} catch(IllegalStateException e) {
			// ok
		}
		
		lis2.simulateTimeout();
		Assert.assertNull(lis2.getMessage());
		
		
	}
	
	
}
