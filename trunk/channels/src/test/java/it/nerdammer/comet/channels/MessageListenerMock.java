package it.nerdammer.comet.channels;

import java.io.IOException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class MessageListenerMock implements MessageListener {

	private String token;
	
	private String channelID;
	
	private TokenizedChannelService service;
	
	private String message;
	
	private Lock monitor;
	
	private Condition messageArrival;
	
	private Long delay;
	
	public MessageListenerMock(String token) {
		this.token = token;
		this.monitor = new ReentrantLock();
		this.messageArrival = this.monitor.newCondition();
	}
	
	public String getToken() {
		return token;
	}
	
	public String getChannelID() {
		return channelID;
	}

	public void registerChannelService(TokenizedChannelService service, String channelID) {
		this.channelID = channelID;
		this.service = service;
	}

	public void onMessage(String message) throws IOException {
		
		if(delay!=null) {
			try {
				Thread.sleep(delay);
			} catch(InterruptedException e) {
				throw new RuntimeException("Interrupted", e);
			}
		}
		
		try {
			monitor.lock();
			if(this.message!=null) {
				throw new IllegalStateException("Message received twice, previous: " + this.message + ", current: " + message);
			}
			
			this.message = message;
			this.messageArrival.signalAll();
		} finally {
			monitor.unlock();
		}
		
		this.message = message;
		service.removeMessageListener(this);
	}
	
	public String waitForMessage() {
		return this.waitForMessage(5000);
	}
	
	public String waitForMessage(long millis) {
		try {
			monitor.lock();
			
			long deadline = System.currentTimeMillis() + millis;
			while(this.message==null) {
				try {
					this.messageArrival.await(millis, TimeUnit.MILLISECONDS);
				} catch(InterruptedException e) {
					throw new RuntimeException("Interrupted", e);
				}
				long time = System.currentTimeMillis();
				if(time>=deadline) {
					throw new IllegalStateException("No message arrived here");
				}
				
				millis = deadline - time;
			}
			
			return this.message;
			
		} finally {
			monitor.unlock();
		}
	}
	
	public Long getDelay() {
		return delay;
	}
	
	public void setDelay(Long delay) {
		this.delay = delay;
	}
	
}
