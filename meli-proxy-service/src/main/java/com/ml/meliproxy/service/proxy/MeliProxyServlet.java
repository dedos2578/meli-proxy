package com.ml.meliproxy.service.proxy;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.eclipse.jetty.client.HttpClient;
import org.eclipse.jetty.http.HttpStatus;
import org.eclipse.jetty.proxy.ProxyServlet;
import org.eclipse.jetty.util.ssl.SslContextFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.ml.meliproxy.service.component.AccessControlBean;
import com.ml.meliproxy.service.component.ReporterBean;
import com.ml.meliproxy.service.component.RulesBean;

@Component
public class MeliProxyServlet extends ProxyServlet.Transparent {
	private static final long serialVersionUID = 1L;

	@Autowired
	private AccessControlBean accessControlBean;

	@Autowired
	private RulesBean counterBean;

	@Autowired
	private ReporterBean reporterBean;

	@Override
	protected HttpClient newHttpClient() {
		SslContextFactory factory = new SslContextFactory();
		factory.setTrustAll(true);
		return new HttpClient(factory);
	}

	@Override
	protected void service(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		Long proxyStart = System.currentTimeMillis();
		Long serviceStart = null;

		String ip = getClientIpAddr(request);
		String path = request.getRequestURI();
		boolean blocked = this.accessControlBean.isBlocked(ip, path);

		if (blocked) {
			response.setStatus(HttpStatus.FORBIDDEN_403);
		} else {
			this.counterBean.increment(ip, path);
			this.counterBean.checkLimits(ip, path);

			serviceStart = System.currentTimeMillis();
			super.service(request, response);
		}

		reporterBean.report(ip, path, fullPath(request), request.getMethod(), response.getStatus(), blocked,
				serviceStart, proxyStart);
	}

	private String getClientIpAddr(HttpServletRequest request) {
		String ips = request.getHeader("X-Forwarded-For");
		if (StringUtils.isNotBlank(ips)) {
			return ips.split(",")[0];
		}
		return request.getRemoteAddr();
	}

	private String fullPath(HttpServletRequest request) {
		String uri = request.getRequestURI();
		String queryString = request.getQueryString();
		return uri + (StringUtils.isNotBlank(queryString) ? "?" + queryString : "");
	}
}