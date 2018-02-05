package com.ml.meliproxy.reports;

import java.util.EnumSet;
import java.util.TimeZone;

import javax.servlet.DispatcherType;

import org.eclipse.jetty.server.Connector;
import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;
import org.eclipse.jetty.server.handler.gzip.GzipHandler;
import org.eclipse.jetty.servlet.FilterHolder;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.springframework.web.context.ContextLoaderListener;
import org.springframework.web.context.support.AnnotationConfigWebApplicationContext;
import org.springframework.web.filter.CharacterEncodingFilter;
import org.springframework.web.servlet.DispatcherServlet;

import com.ml.meliproxy.reports.context.AppContext;
import com.ml.meliproxy.reports.context.Constants;

public class WebServer {
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
		if (args.length != 1) {
			throw new IllegalArgumentException("Some args are mandatory '<port number>'");
		}

		int port = Integer.parseInt(args[0]);

		TimeZone.setDefault(TimeZone.getTimeZone(GMT));
		Server server = new Server();

		ServerConnector connector = new ServerConnector(server);
		connector.setPort(port);
		server.setConnectors(new Connector[] { connector });

		server.setHandler(this.buildAppContext());
		server.setStopAtShutdown(true);
		return server;
	}

	private void run() throws Exception {
		this.server.start();
		this.server.join();
	}

	private Handler buildAppContext() {
		AnnotationConfigWebApplicationContext applicationContext = new AnnotationConfigWebApplicationContext();
		applicationContext.register(AppContext.class);

		ServletContextHandler handler = new ServletContextHandler(ServletContextHandler.NO_SESSIONS);
		handler.setContextPath(Constants.DEFAULT_CONTEXT_PATH);

		this.appendSpringDispatcherServlet(applicationContext, handler);
		this.appendListeners(applicationContext, handler);
		this.appendFilters(handler);

		GzipHandler gzipHandler = new GzipHandler();
		gzipHandler.setHandler(handler);

		applicationContext.close();
		return gzipHandler;
	}

	private void appendSpringDispatcherServlet(AnnotationConfigWebApplicationContext applicationContext,
			ServletContextHandler handler) {
		DispatcherServlet dispatcherServlet = new DispatcherServlet(applicationContext);
		dispatcherServlet.setDispatchOptionsRequest(true);
		ServletHolder servletHolder = new ServletHolder(dispatcherServlet);
		servletHolder.setName("spring");
		servletHolder.setInitOrder(1);
		handler.addServlet(servletHolder, "/*");
	}

	private void appendListeners(AnnotationConfigWebApplicationContext applicationContext,
			ServletContextHandler handler) {
		handler.addEventListener(new ContextLoaderListener(applicationContext));
	}

	private void appendFilters(ServletContextHandler handler) {
		FilterHolder characterEncodingFilterHolder = new FilterHolder(new CharacterEncodingFilter());
		handler.addFilter(characterEncodingFilterHolder, "/*",
				EnumSet.of(DispatcherType.REQUEST, DispatcherType.ERROR));
	}
}
