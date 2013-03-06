package it.nerdammer.comet.channels;

import java.io.IOException;

import javax.servlet.AsyncContext;
import javax.servlet.AsyncEvent;
import javax.servlet.AsyncListener;

class AsyncContextMessageListener implements MessageListener {

	private AsyncContext ctx;
	
	private String token;
	
	private String channelID;
	
	public AsyncContextMessageListener(AsyncContext ctx, String token) {
		if(ctx==null) {
			throw new IllegalArgumentException("Null context");
		}
		if(token==null) {
			throw new IllegalArgumentException("Null token");
		}
		this.ctx = ctx;
		this.token = token;
	}
	
	public String getToken() {
		return token;
	}
	
	public String getChannelID() {
		return channelID;
	}
	
	public void registerChannelService(final TokenizedChannelService service, String channelID) {
		this.channelID = channelID;
		this.ctx.addListener(new AsyncListener() {
			
			public void onTimeout(AsyncEvent arg0) throws IOException {
				service.removeMessageListener(AsyncContextMessageListener.this);
			}
			
			public void onStartAsync(AsyncEvent arg0) throws IOException {
				
			}
			
			public void onError(AsyncEvent arg0) throws IOException {
				service.removeMessageListener(AsyncContextMessageListener.this);
			}
			
			public void onComplete(AsyncEvent arg0) throws IOException {
			}
		});
	}
	
	public void onMessage(String message) throws IOException {
		this.ctx.getResponse().setCharacterEncoding("UTF-8");
		this.ctx.getResponse().getWriter().println(message);
		this.ctx.complete();
	}
	
}
