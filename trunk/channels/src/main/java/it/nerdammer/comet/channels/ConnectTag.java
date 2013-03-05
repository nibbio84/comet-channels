package it.nerdammer.comet.channels;

import java.net.URLEncoder;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.TagSupport;

public class ConnectTag extends TagSupport {
	
	private static final long serialVersionUID = 1L;
	
	private static final String SKIP_LIBRARY_PROPERTY = "it.nerdammer.comet.channels.SKIP_LIBRARY";
	
	private String channel;
	
	private String mode;
	
	private String callback;

	private String sender;
	
	@Override
	public int doStartTag() throws JspException {
		
		try {
			String contextPath = ((HttpServletRequest)pageContext.getRequest()).getContextPath();
			
			boolean readAllowed;
			boolean writeAllowed;
			
			if(mode==null || mode.equalsIgnoreCase("r") || mode.equalsIgnoreCase("read")) {
				readAllowed = true;
				writeAllowed = false;
			} else if(mode.equalsIgnoreCase("w") || mode.equalsIgnoreCase("write")) {
				readAllowed = false;
				writeAllowed = true;
			} else if(mode.equalsIgnoreCase("rw") || mode.equalsIgnoreCase("wr")) {
				readAllowed = true;
				writeAllowed = true;
			} else {
				throw new IllegalArgumentException("Unknown mode " + mode + ". Only \"R\", \"READ\", \"W\", \"WRITE\", \"RW\", or \"WR\" are allowed");
			}
			
			
			String readToken = null;
			if(readAllowed) {
				// listen to the channel
				if(callback==null) {
					throw new IllegalArgumentException("If read mode is enabled, the callback parameter is mandatory");
				}
				
				String checkCallback = callback.replaceAll("[A-Za-z_]", "");
				if(checkCallback.length()>0) {
					throw new IllegalArgumentException("The callback parameter is not a valid javascript function name");
				}
				
				readToken = ((TokenizedChannelService)ChannelServiceFactory.getChannelService()).createReadToken(channel);
			}
			
			
			String writeToken = null;
			if(writeAllowed) {
				// create a sender
				if(sender==null) {
					throw new IllegalArgumentException("If write mode is enabled, the sender parameter is mandatory");
				}
				
				String checkSender = sender.replaceAll("[A-Za-z_]", "");
				if(checkSender.length()>0) {
					throw new IllegalArgumentException("The sender parameter is not a valid javascript variable name");
				}
				
				
				writeToken = ((TokenizedChannelService)ChannelServiceFactory.getChannelService()).createWriteToken(channel);
			}
			
				
			Boolean skipLibrary = false;
			Boolean skipProp = (Boolean) pageContext.getAttribute(SKIP_LIBRARY_PROPERTY);
			if(skipProp!=null && skipProp) {
				skipLibrary = true;
			}
			pageContext.setAttribute(SKIP_LIBRARY_PROPERTY, true);
				
			pageContext.getOut().print("<script type=\"text/javascript\" src=\"" + contextPath + "/comet-channels/client.js?load=true");
			
			if(readToken!=null) {
				pageContext.getOut().print("&read_token=" + URLEncoder.encode(readToken, "UTF-8"));
			}
			
			if(callback!=null) {
				pageContext.getOut().print("&callback_function=" + URLEncoder.encode(callback, "UTF-8"));
			}
			
			if(writeToken!=null) {
				pageContext.getOut().print("&write_token=" + URLEncoder.encode(writeToken, "UTF-8"));
			}
			
			if(sender!=null) {
				pageContext.getOut().print("&sender=" + URLEncoder.encode(sender, "UTF-8"));
			}
				
			if(skipLibrary) {
				pageContext.getOut().print("&skip_library=true");
			}
				
			pageContext.getOut().println("\"></script>");
			
		} catch(Exception e) {
			Logger.getAnonymousLogger().log(Level.SEVERE, "Error while registering channel", e);
		}
		
		return SKIP_BODY;
	}
	
	public void setChannel(String channel) {
		this.channel = channel;
	}
	
	public String getChannel() {
		return channel;
	}
	
	public void setCallback(String callback) {
		this.callback = callback;
	}
	
	public String getCallback() {
		return callback;
	}
	
	public void setMode(String mode) {
		this.mode = mode;
	}
	
	public String getMode() {
		return mode;
	}
	
	public void setSender(String sender) {
		this.sender = sender;
	}
	
	public String getSender() {
		return sender;
	}

}
