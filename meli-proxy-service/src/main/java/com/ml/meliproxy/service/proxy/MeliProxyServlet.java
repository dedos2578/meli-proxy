package com.ml.meliproxy.service.proxy;

import javax.servlet.http.HttpServletRequest;

import org.eclipse.jetty.client.HttpClient;
import org.eclipse.jetty.proxy.ProxyServlet;
import org.eclipse.jetty.util.ssl.SslContextFactory;

public class MeliProxyServlet extends ProxyServlet.Transparent {

	private static final long serialVersionUID = 1L;

	@Override
    protected HttpClient newHttpClient() {
        SslContextFactory factory = new SslContextFactory();
        factory.setTrustAll(true);
        return new HttpClient(factory);
    }

    @Override
    protected String rewriteTarget(HttpServletRequest request) {
        return super.rewriteTarget(request);
    }
}