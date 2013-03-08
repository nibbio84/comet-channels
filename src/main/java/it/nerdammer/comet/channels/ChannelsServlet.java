package it.nerdammer.comet.channels;

import java.io.IOException;

import javax.servlet.AsyncContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class ChannelsServlet extends HttpServlet {

	private static final long serialVersionUID = 1L;

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse res)
			throws ServletException, IOException {
		
		String token = req.getParameter("token");
		AsyncContext ctx = req.startAsync();
		ctx.setTimeout(60000);
		
		TokenizedChannelService service = (TokenizedChannelService) ChannelServiceFactory.getChannelService();
		service.addMessageListener(new AsyncContextMessageListener(ctx, token));
		
	}
	
	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
		
		String unparsedMessage = req.getParameter("message");
		String token = req.getParameter("token");
		
		TokenizedChannelService service = (TokenizedChannelService) ChannelServiceFactory.getChannelService();
		service.sendUnparsedMessage(token, unparsedMessage);
	}
	
}
