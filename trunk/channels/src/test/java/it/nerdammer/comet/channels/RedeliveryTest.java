package it.nerdammer.comet.channels;

import org.junit.Assert;
import org.junit.Test;

public class RedeliveryTest {

	@Test
	public void testOneRedelivery() throws InterruptedException {
		
		TokenizedChannelService service = new MultiThreadedChannelService(10);
		
		String token = service.createReadToken("canale1");
		
		MessageListenerMock lis1 = new MessageListenerMock(token);
		service.addMessageListener(lis1);
		
		service.sendMessage("ciccia", "canale1");
		
		String wrapped = lis1.waitForMessage();
		String message = TestUtil.unwrapJsonString(wrapped);

		Assert.assertEquals("ciccia", message);
		
		service.sendMessage("ciccia2", "canale1");
		Thread.sleep(150);
		
		MessageListenerMock lis2 = new MessageListenerMock(token);
		service.addMessageListener(lis2);
		
		String wrapped2 = lis2.waitForMessage();
		String message2 = TestUtil.unwrapJsonString(wrapped2);

		Assert.assertEquals("ciccia2", message2);
		
		
	}
	
	
	@Test
	public void testTwoRedeliveries() throws InterruptedException {
		
		TokenizedChannelService service = new MultiThreadedChannelService(10);
		
		String token = service.createReadToken("canale1");
		
		MessageListenerMock lis1 = new MessageListenerMock(token);
		service.addMessageListener(lis1);
		
		service.sendMessage("ciccia", "canale1");
		
		String wrapped = lis1.waitForMessage();
		String message = TestUtil.unwrapJsonString(wrapped);

		Assert.assertEquals("ciccia", message);
		
		service.sendMessage("ciccia2", "canale1");
		service.sendMessage("ciccia3", "canale1");
		Thread.sleep(150);
		
		MessageListenerMock lis2 = new MessageListenerMock(token);
		service.addMessageListener(lis2);
		
		String wrapped2 = lis2.waitForMessage();
		String message2 = TestUtil.unwrapJsonString(wrapped2);

		Assert.assertEquals("ciccia2", message2);
		
		MessageListenerMock lis3 = new MessageListenerMock(token);
		service.addMessageListener(lis3);
		
		String wrapped3 = lis3.waitForMessage();
		String message3 = TestUtil.unwrapJsonString(wrapped3);

		Assert.assertEquals("ciccia3", message3);
		
		
	}
	
	
	
}
