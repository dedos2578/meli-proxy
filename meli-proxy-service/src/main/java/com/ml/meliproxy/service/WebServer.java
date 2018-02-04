package com.ml.meliproxy.service;

import java.util.TimeZone;

import org.eclipse.jetty.server.Connector;
import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;
import org.eclipse.jetty.server.handler.gzip.GzipHandler;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import com.ml.meliproxy.service.context.AppContext;
import com.ml.meliproxy.service.context.Constants;
import com.ml.meliproxy.service.proxy.MeliProxyServlet;

public class WebServer {
	private static final Logger LOGGER = LoggerFactory.getLogger(WebServer.class);

	private static final String GMT = "GMT";

	private final Server server;

	public WebServer(String[] args) {
		this.server = this.createNewServer(args);
	}

	public static void main(String[] args) throws Exception {
		WebServer server = new WebServer(args);
		server.run();
	}

	private Server createNewServer(String[] args) {
		if (args.length != 2) {
			throw new IllegalArgumentException("Some args are mandatory '<port number> <proxyTo URL>'");
		}
		int port = Integer.parseInt(args[0]);
		String proxyTo = args[1];
		LOGGER.info("Proxying {} at {}", proxyTo, port);

		TimeZone.setDefault(TimeZone.getTimeZone(GMT));
		Server server = new Server();

		ServerConnector connector = new ServerConnector(server);
		connector.setPort(port);
		server.setConnectors(new Connector[] { connector });

		// Init spring context
		ApplicationContext applicationContext = new AnnotationConfigApplicationContext(AppContext.class);

		server.setHandler(this.buildMeliProxyServletHandler(proxyTo, applicationContext));
		server.setStopAtShutdown(true);
		return server;
	}

	private void run() throws Exception {
		this.server.start();
		this.server.join();
	}

	private Handler buildMeliProxyServletHandler(String proxyTo, ApplicationContext applicationContext) {
		ServletContextHandler handler = new ServletContextHandler(ServletContextHandler.NO_SESSIONS);
		handler.setContextPath(Constants.DEFAULT_CONTEXT_PATH);

		MeliProxyServlet meliProxyServlet = applicationContext.getBean(MeliProxyServlet.class);
		ServletHolder servletHolder = new ServletHolder(meliProxyServlet);
		servletHolder.setName("proxyServlet");
		servletHolder.setInitOrder(1);
		servletHolder.setInitParameter("proxyTo", proxyTo);
		handler.addServlet(servletHolder, "/*");

		GzipHandler gzipHandler = new GzipHandler();
		gzipHandler.setHandler(handler);
		return gzipHandler;
	}
}
