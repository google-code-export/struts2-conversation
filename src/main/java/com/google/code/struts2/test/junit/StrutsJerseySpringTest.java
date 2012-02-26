package com.google.code.struts2.test.junit;

import java.util.HashMap;
import java.util.Map;

import org.junit.After;
import org.junit.Before;
import org.mortbay.jetty.Server;
import org.mortbay.jetty.servlet.Context;
import org.mortbay.jetty.servlet.ServletHolder;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.web.context.ContextLoaderListener;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.request.RequestContextListener;
import org.springframework.web.context.support.WebApplicationContextUtils;

import com.google.code.struts2.test.junit.StringUtil;
import com.google.code.struts2.test.junit.StrutsTest;
import com.opensymphony.xwork2.util.logging.Logger;
import com.opensymphony.xwork2.util.logging.LoggerFactory;
import com.sun.jersey.api.core.PackagesResourceConfig;
import com.sun.jersey.spi.container.servlet.ServletContainer;
import com.sun.jersey.spi.spring.container.servlet.SpringServlet;

public abstract class StrutsJerseySpringTest<T> extends StrutsTest<T> {
	
	private static final Logger LOG = LoggerFactory.getLogger(StrutsJerseySpringTest.class);
	
	public static final int DEFAULT_PORT = 9992;
	public static final String DEFAULT_HOST = "http://localhost:" + DEFAULT_PORT;
	public static final String DEFAULT_SPRING_CONTEXT_LOCATION = "classpath:applicationContext.xml";
	public static final String DEFAULT_SERVLET_MAPPING = "/*";
	public static final String DEFAULT_BASE_SERVICES_PATH = "/";
	
	protected Server server;
	protected ApplicationContext applicationContext;
	protected RestConfig restConfig;

	@Before
	@Override
	public void setUp() throws Exception {
		processRestConfig();
		initServer();
		injectDependencies();
		super.setUp();
	}

	@After
	@Override
	public void tearDown() throws Exception {
		destroyServer();
		super.tearDown();
	}
	
	@Override
	protected void setupBeforeInitDispatcher() {
		 this.servletContext.setAttribute(WebApplicationContext.ROOT_WEB_APPLICATION_CONTEXT_ATTRIBUTE, applicationContext);
	}
	
	protected void processRestConfig() {
		if (this.getClass().isAnnotationPresent(RestConfig.class)) {
			restConfig = this.getClass().getAnnotation(RestConfig.class);
		} else {
			LOG.warn("No RestConfig declared.  Use @RestConfig annotation to set configuration.  Using defaults.");
		}
	}
	
	protected void injectDependencies() {
		applicationContext.getAutowireCapableBeanFactory().autowireBean(this);
	}
	
	protected void initServer() throws Exception {
		LOG.info("Initializing Jetty server...");
		ServletHolder sh = new ServletHolder(ServletContainer.class);
        sh.setInitParameter(ServletContainer.RESOURCE_CONFIG_CLASS, PackagesResourceConfig.class.getName());
        sh.setInitParameter(PackagesResourceConfig.PROPERTY_PACKAGES, getPackages());
        sh.setServlet(new SpringServlet());
        
        server = new Server(getPort());
        Context context = new Context(server, getBasePath(), Context.SESSIONS);
        Map<String, String> params = new HashMap<String, String>();
        params.put(ContextLoaderListener.CONFIG_LOCATION_PARAM, getSpringContextLocation());
        context.setInitParams(params);
        context.addEventListener(new ContextLoaderListener());
        context.addEventListener(new RequestContextListener());
        context.addServlet(sh, getServletMapping());
        server.start();
        LOG.info("Jetty server running.");
        this.applicationContext = WebApplicationContextUtils.getWebApplicationContext(context.getServletContext());
	}
	
	protected void destroyServer() throws Exception {
		LOG.info("Stopping Jetty server...");
		server.stop();
		LOG.info("Jetty server stopped...");
		LOG.info("Destroying Jetty server...");
		server.destroy();
		LOG.info("Jetty server destroyed.");
	}
	
	protected String getPackages() {
		return this.getClass().getPackage().getName();
	}
	
	protected String getBasePath() {
		String basePath = DEFAULT_BASE_SERVICES_PATH;
		if (restConfig != null) {
			basePath = restConfig.baseServicesPath();
		}
		return basePath;
	}
	
	protected String getHost() {
		return DEFAULT_HOST;
	}
	
	protected int getPort() {
		return DEFAULT_PORT;
	}
	
	protected String getSpringContextLocation() {
		if (this.getClass().isAnnotationPresent(ContextConfiguration.class)) {
			ContextConfiguration config = this.getClass().getAnnotation(ContextConfiguration.class);
			String[] locations = config.locations();
			return StringUtil.unsplit(",", locations);
		} else {
			return DEFAULT_SPRING_CONTEXT_LOCATION;
		}
	}
	
	protected String getServletMapping() {
		return DEFAULT_SERVLET_MAPPING;
	}

}
