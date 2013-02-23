package it.nerdammer.comet.channels;

import java.io.IOException;
import java.util.Collection;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.logging.Level;
import java.util.logging.Logger;

public class MultiThreadedChannelService extends AbstractChannelService {

	protected ScheduledThreadPoolExecutor executor;
	
	public MultiThreadedChannelService(int corePoolSize) {
		this.executor = new ScheduledThreadPoolExecutor(corePoolSize);
	}
	
	@Override
	protected void doSendMessage(final String json, Collection<MessageListener> listeners) {
	
		for(MessageListener ls : listeners) {
			final MessageListener finLs = ls;
			executor.submit(new Runnable() {
				public void run() {
					try {
						finLs.onMessage(json);

					} catch (IOException e) {
						Logger.getAnonymousLogger()
								.info("Channel with token " + finLs.getToken()
										+ " disconnected");
					} catch (Throwable e) {
						Logger.getAnonymousLogger().log(Level.WARNING,
								"Error on channel with token " + finLs.getToken(), e);
					}
				}
			});
			
		}
		
	}
	
}
