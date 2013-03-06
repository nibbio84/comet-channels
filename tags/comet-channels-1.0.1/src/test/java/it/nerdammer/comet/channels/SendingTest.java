package it.nerdammer.comet.channels;

import java.util.LinkedList;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;

public class SendingTest {

	@Test
	public void testOneListener() {
		
		TokenizedChannelService service = new DefaultChannelService(10);
		
		String token = service.createReadToken("canale1");
		
		MessageListenerMock lis1 = new MessageListenerMock(token);
		service.addMessageListener(lis1);
		
		service.sendMessage("ciccia", "canale1");
		
		String wrapped = lis1.waitForMessage();
		String message = TestUtil.unwrapJsonString(wrapped);
		
		Assert.assertEquals("ciccia", message);
		
	}
	
	@Test
	public void testTwoListeners() {
		
		TokenizedChannelService service = new DefaultChannelService(10);
		
		String token = service.createReadToken("canale1");
		
		MessageListenerMock lis1 = new MessageListenerMock(token);
		service.addMessageListener(lis1);
		
		MessageListenerMock lis2 = new MessageListenerMock(token);
		service.addMessageListener(lis2);
		
		service.sendMessage("ciccia", "canale1");
		
		String wrapped1 = lis1.waitForMessage();
		String message1 = TestUtil.unwrapJsonString(wrapped1);
		
		String wrapped2 = lis2.waitForMessage();
		String message2 = TestUtil.unwrapJsonString(wrapped2);
		
		Assert.assertEquals("ciccia", message1);
		Assert.assertEquals("ciccia", message2);
		
	}
	
	@Test
	public void testMultListeners() {
		
		TokenizedChannelService service = new DefaultChannelService(10);
		
		String token = service.createReadToken("canale1");
		
		List<MessageListenerMock> listeners = new LinkedList<MessageListenerMock>();
		for(int i=0; i<100; i++) {
			MessageListenerMock lis = new MessageListenerMock(token);
			service.addMessageListener(lis);
			
			listeners.add(lis);
		}
		
		service.sendMessage("ciccia", "canale1");
		
		for(MessageListenerMock lis : listeners) {
			String wrapped = lis.waitForMessage();
			String message = TestUtil.unwrapJsonString(wrapped);
		
			Assert.assertEquals("ciccia", message);
		}
		
	}
	
	
	@Test
	public void testMultListenersMultiMessages() {
		
		TokenizedChannelService service = new DefaultChannelService(35);
		
		String token1 = service.createReadToken("canale1");
		String token2 = service.createReadToken("canale2");
		
		List<MessageListenerMock> listeners = new LinkedList<MessageListenerMock>();
		for(int i=0; i<1000; i++) {
			MessageListenerMock lis = new MessageListenerMock(i%2==0 ? token1 : token2);

			if(i%3==0) {
				lis.setDelay(10L);
			}
			
			service.addMessageListener(lis);
			
			listeners.add(lis);
		}
		
		service.sendMessage("ciccia", "canale1");
		service.sendMessage("cicciaciccia", "canale2");
		
		int idx=0;
		for(MessageListenerMock lis : listeners) {
			String expected = idx%2==0 ? "ciccia" : "cicciaciccia";
			String wrapped = lis.waitForMessage();
			String message = TestUtil.unwrapJsonString(wrapped);
		
			Assert.assertEquals(expected, message);
			
			idx++;
		}
		
	}
	
	
}
