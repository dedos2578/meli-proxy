package com.ml.meliproxy.service;

import java.io.IOException;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.eclipse.jetty.http.HttpStatus;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.ml.meliproxy.service.mock.HttpServletResponseMock;
import com.ml.meliproxy.service.proxy.MeliProxyServlet;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = { TestContext.class })
public class MeliProxyServletTest {

	@Autowired
	private MeliProxyServlet meliProxyServlet;
	
	@Before
	public void init() throws ServletException {
		ServletContext servletContext = Mockito.mock(ServletContext.class);
		Mockito.when(servletContext.getAttribute(Mockito.eq("org.eclipse.jetty.server.Executor"))).thenReturn(Executors.newSingleThreadExecutor());

		ServletConfig mock = Mockito.mock(ServletConfig.class);
		Mockito.when(mock.getInitParameter(Mockito.eq("proxyTo"))).thenReturn("https://api.mercadolibre.com");
		Mockito.when(mock.getInitParameter(Mockito.eq("prefix"))).thenReturn("/");
		Mockito.when(mock.getServletContext()).thenReturn(servletContext);
		Mockito.when(mock.getServletName()).thenReturn("Name");
		meliProxyServlet.init(mock);
	}

	@Test
	public void serviceTest() throws ServletException, IOException {
		HttpServletRequest request = Mockito.mock(HttpServletRequest.class);
		Mockito.when(request.getRequestURI()).thenReturn("/test/123");
		Mockito.when(request.getRemoteAddr()).thenReturn("192.168.1.11");

		HttpServletResponse response = new HttpServletResponseMock();
		meliProxyServlet.service(request, response);

		Assert.assertEquals(HttpStatus.OK_200, response.getStatus());
	}
	
	@Test
	public void blockedServiceTest() throws ServletException, IOException {
		HttpServletRequest request = Mockito.mock(HttpServletRequest.class);
		Mockito.when(request.getRequestURI()).thenReturn("/test/123");
		Mockito.when(request.getRemoteAddr()).thenReturn("192.168.1.10");

		HttpServletResponse response = new HttpServletResponseMock();
		meliProxyServlet.service(request, response);

		Assert.assertEquals(HttpStatus.FORBIDDEN_403, response.getStatus());
	}
	
	@Test
	public void blockedServiceTest2() throws ServletException, IOException, InterruptedException {
		HttpServletRequest request = Mockito.mock(HttpServletRequest.class);
		Mockito.when(request.getRequestURI()).thenReturn("/byPath");
		Mockito.when(request.getRemoteAddr()).thenReturn("192.168.1.99");

		HttpServletResponse response = new HttpServletResponseMock();
		meliProxyServlet.service(request, response);
		Assert.assertEquals(HttpStatus.OK_200, response.getStatus());
		TimeUnit.MILLISECONDS.sleep(10); // Simula el tiempo de ejecucion del servicio

		meliProxyServlet.service(request, response);
		Assert.assertEquals(HttpStatus.OK_200, response.getStatus());
		TimeUnit.MILLISECONDS.sleep(10); // Simula el tiempo de ejecucion del servicio

		meliProxyServlet.service(request, response);
		Assert.assertEquals(HttpStatus.FORBIDDEN_403, response.getStatus());
	}
	
	@Test
	public void blockedServiceTest3() throws ServletException, IOException, InterruptedException {
		HttpServletRequest request = Mockito.mock(HttpServletRequest.class);
		Mockito.when(request.getRequestURI()).thenReturn("/byIpAndPath");
		Mockito.when(request.getRemoteAddr()).thenReturn("192.168.1.9");
		HttpServletResponse response = new HttpServletResponseMock();
		
		for (int i = 0; i < 50; i++) {
			meliProxyServlet.service(request, response);
			TimeUnit.MILLISECONDS.sleep(10); // Simula el tiempo de ejecucion del servicio
			Assert.assertEquals(HttpStatus.OK_200, response.getStatus());
		}
		
		meliProxyServlet.service(request, response);
		Assert.assertEquals(HttpStatus.FORBIDDEN_403, response.getStatus());
	}
}
