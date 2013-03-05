package it.nerdammer.comet.channels;

import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.logging.Level;
import java.util.logging.Logger;

class DefaultChannelService implements TokenizedChannelService {

	private Map<String, String> readTokenMap;
	private Map<String, String> writeTokenMap;
	
	private Map<String, Set<MessageListener>> messageListeners;
	
	private Map<String, Long> removedReadTokens;
	private Map<String, List<String>> undeliveredMessages;
	
	private ScheduledThreadPoolExecutor executor;
	
	private long cleanupTime = 10000;
	
	
	public DefaultChannelService(int corePoolSize) {
		this.readTokenMap = new HashMap<String, String>();
		this.writeTokenMap = new HashMap<String, String>();
		
		this.messageListeners = new HashMap<String, Set<MessageListener>>();
		
		this.removedReadTokens = new HashMap<String, Long>();
		this.undeliveredMessages = new HashMap<String, List<String>>();
		
		this.executor = new ScheduledThreadPoolExecutor(corePoolSize);
	}
	
	public synchronized String createReadToken(String channelID) {
		if(channelID==null) {
			throw new IllegalArgumentException("Please, provide a valid channelID");
		}
		
		String token = UUID.randomUUID().toString();
		readTokenMap.put(token, channelID);

		return token;
	}
	
	public synchronized String createWriteToken(String channelID) {
		if(channelID==null) {
			throw new IllegalArgumentException("Please, provide a valid channelID");
		}
		
		String token = UUID.randomUUID().toString();
		writeTokenMap.put(token, channelID);
		return token;
	}
	
	protected synchronized String getChannelIDForReading(String token) {
		String channelID;
		channelID = readTokenMap.get(token);
		return channelID;
	}
	
	protected synchronized String getChannelIDForWriting(String token) {
		String channelID;
		channelID = writeTokenMap.get(token);
		return channelID;
	}
	
	public synchronized void sendMessage(Object jsonMessage, String channelID) {
		if(channelID==null) {
			throw new IllegalArgumentException("Please, provide a valid channelID");
		}
		if(jsonMessage==null) {
			throw new IllegalArgumentException("Please, provide a valid jsonMessage");
		}
		String json = ChannelServiceFactory.getMessageConverter().convert(jsonMessage, channelID);

		checkAndAddToUndelivered(json, channelID);
		
		Set<MessageListener> listeners = getMessageListeners(channelID);
		for(MessageListener listener : listeners) {
			removeMessageListener(listener);
			doSendMessage(json, listener);
		}
	}
	
	public synchronized void sendUnparsedMessage(String token, String unparsedMessage) {
		
		String channelID = getChannelIDForWriting(token);
		
		if(channelID==null) {
			throw new IllegalArgumentException("The token is not associated with a valid channelID");
		}
		if(unparsedMessage==null) {
			throw new IllegalArgumentException("Please, provide a valid unparsedMessage");
		}
		
		String  json = ChannelServiceFactory.getMessageConverter().convertUnparsedJson(unparsedMessage, channelID);
		
		checkAndAddToUndelivered(json, channelID);
		
		Set<MessageListener> listeners = getMessageListeners(channelID);
		for(MessageListener listener : listeners) {
			removeMessageListener(listener);
			doSendMessage(json, listener);
		}
	}
	
	protected synchronized void checkAndAddToUndelivered(String message, String channelID) {
		cleanupListeners();
		
		for(String token : this.removedReadTokens.keySet()) {
			String chan = this.readTokenMap.get(token);
			if(chan.equals(channelID)) {
				List<String> undel = this.undeliveredMessages.get(token);
				if(undel==null) {
					undel = new LinkedList<String>();
					this.undeliveredMessages.put(token, undel);
				}
				
				undel.add(message);
			}
		}
	}
	
	public synchronized void addMessageListener(MessageListener listener) {
		String token = listener.getToken();
		String channelID = getChannelIDForReading(token);
		Set<MessageListener> channelSet = messageListeners.get(channelID);
		
		if(channelSet!=null) {
			for(MessageListener lis : this.messageListeners.get(channelID)) {
				if(lis.equals(listener)) {
					throw new IllegalArgumentException("Listener already registered");
				}
			}
		}
		
		cleanupListeners();
		
		
		if(channelID==null) {
			throw new IllegalArgumentException("The token is not associated with a valid channelID");
		}
		
		
		if(channelSet==null) {
			channelSet = new HashSet<MessageListener>();
			messageListeners.put(channelID, channelSet);
		}
		
		channelSet.add(listener);

		listener.registerChannelService(this, channelID);
		
		
		// Submit immediately undelivered messages
		List<String> undelivered = this.undeliveredMessages.get(token);
		if(undelivered!=null && undelivered.size()>0) {
			String message = undelivered.remove(0);
			removeMessageListener(listener);
			doSendMessage(message, listener);
			return;
		}
	}
	
	public synchronized void removeMessageListener(MessageListener listener) {
		String channelId = listener.getChannelID();
		if(channelId==null) {
			throw new IllegalArgumentException("The listener doesn't have an associated channelID");
		}
		if(!messageListeners.get(channelId).contains(listener)) {
			throw new IllegalArgumentException("The listener has not been registered yet");
		}
		
		
		cleanupListeners();
		
		String token = listener.getToken();
		this.removedReadTokens.put(token, System.currentTimeMillis());
		messageListeners.get(channelId).remove(listener);
	}
	
	protected synchronized void cleanupListeners() {
		long time = System.currentTimeMillis();
		Set<String> tokenToRemove = new HashSet<String>();
		for(String tk : removedReadTokens.keySet()) {
			long removalDate = removedReadTokens.get(tk);
			if(removalDate+this.cleanupTime < time) {
				tokenToRemove.add(tk);
			}
		}
		
		for(String tk : tokenToRemove) {
			removedReadTokens.remove(tk);
			undeliveredMessages.remove(tk);
			//readTokenMap.remove(tk); // Don't remove
		}
	}
	
	protected synchronized Set<MessageListener> getMessageListeners(String channelID) {
		Set<MessageListener> listeners;
		Set<MessageListener> contextsRef = messageListeners.get(channelID);
		if(contextsRef!=null) {
			listeners = new HashSet<MessageListener>(contextsRef);
		} else {
			listeners = Collections.emptySet();
		}
		
		return listeners;
	}
	
	protected void doSendMessage(final String json, final MessageListener listener) {
	
			executor.submit(new Runnable() {
				public void run() {
					try {
						listener.onMessage(json);

					} catch (IOException e) {
						Logger.getAnonymousLogger()
								.info("Channel with token " + listener.getToken()
										+ " disconnected");
					} catch (Throwable e) {
						Logger.getAnonymousLogger().log(Level.WARNING,
								"Error on channel with token " + listener.getToken(), e);
					}
				}
			});
		
	}
	
	public void setCleanupTime(long cleanupTime) {
		this.cleanupTime = cleanupTime;
	}
	
	public long getCleanupTime() {
		return cleanupTime;
	}
	
}
