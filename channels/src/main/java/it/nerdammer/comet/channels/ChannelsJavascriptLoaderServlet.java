package it.nerdammer.comet.channels;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringWriter;
import java.util.concurrent.atomic.AtomicReference;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class ChannelsJavascriptLoaderServlet extends HttpServlet {
	
	private static final long serialVersionUID = 1L;
	
	private static AtomicReference<String> clientJS = new AtomicReference<String>();
	
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
		
		String skipLibrary = req.getParameter("skip_library");
		
		if(!"true".equals(skipLibrary)) {
			String jsLib = getClientJSCode(req);
			res.getWriter().println(jsLib);
		}
		
		res.setContentType("application/javascript");
		res.setHeader("Cache-Control", "max-age=86400");
		
		String readToken = req.getParameter("read_token");
		String callbackFunction = req.getParameter("callback_function");
		if(readToken!=null && callbackFunction!=null) {
			
			res.getWriter().println();
			res.getWriter().println("// code for reading");
			res.getWriter().println("$(document).ready(function() {");
			res.getWriter().println("	var token = \"" + readToken.replace("\"", "\\\"") + "\";");
			res.getWriter().println("	initNibbioChannel(token, " +  callbackFunction + ");");
			res.getWriter().println("});");

		} else {
			res.getWriter().println("// read mode disabled");
		}
		
		String writeToken = req.getParameter("write_token");
		String sender = req.getParameter("sender");
		if(writeToken!=null && sender!=null) {
			
			
			res.getWriter().println();
			res.getWriter().println("// code for writing");
			res.getWriter().println("var " + sender + " = new nibbioChannelMessageSender(\"" + writeToken + "\");");
			
		} else {
			res.getWriter().println("// write mode disabled");
		}
		
		
	}
	
	protected String getClientJSCode(HttpServletRequest req) throws IOException {
		if(clientJS.get()!=null) {
			return clientJS.get();
		}
		
		InputStream in = ChannelsJavascriptLoaderServlet.class.getResourceAsStream("client.js");
		Reader re = new InputStreamReader(in);
		StringWriter sw = new StringWriter();
		char[] buffer = new char[2048];
		int read;
		do {
			read = re.read(buffer);
			if(read>0) {
				sw.write(buffer, 0, read);
			}
		} while(read>=0);
		
		String js = sw.toString();
		js = js.replace("${contextPath}", req.getContextPath());
		
		clientJS.set(js);
		return js;
	}

}
