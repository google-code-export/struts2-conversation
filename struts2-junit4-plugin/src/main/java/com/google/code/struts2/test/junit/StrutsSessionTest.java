package com.google.code.struts2.test.junit;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpSession;

import org.apache.commons.lang3.StringUtils;
import org.apache.struts2.dispatcher.Dispatcher;
import org.apache.struts2.dispatcher.SessionMap;
import org.apache.struts2.util.StrutsTestCaseHelper;
import org.junit.AfterClass;
import org.junit.Before;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.core.io.ResourceLoader;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.mock.web.MockServletContext;

import com.opensymphony.xwork2.ActionProxyFactory;
import com.opensymphony.xwork2.config.Configuration;
import com.opensymphony.xwork2.config.ConfigurationManager;
import com.opensymphony.xwork2.inject.Container;
import com.opensymphony.xwork2.util.XWorkTestCaseHelper;

public class StrutsSessionTest<T> extends StrutsTest<T> {

    protected static Configuration configuration;
    protected static ConfigurationManager configurationManager;
    protected static Container container;
    protected static HttpSession httpSession;
    protected static ResourceLoader resourceLoader = new DefaultResourceLoader();
    protected static MockServletContext servletContext;
    protected static Map<String, Object> session;
    protected static ActionProxyFactory actionProxyFactory;

    @Before
    @Override
    public void setUp() throws Exception {
        if (configurationManager == null) {
            servletContext = new MockServletContext(resourceLoader);
            httpSession = new MockHttpSession(servletContext);
            request = new MockHttpServletRequest(servletContext);
            request.setSession(httpSession);
            session = new SessionMap<String, Object>(request);
            setupBeforeInitDispatcher();
            processAnnotations(currentlyExecutingTest.getMethodName());
            if (StringUtils.isNotBlank(getConfigPath())) {
                dispatcherInitParams = new HashMap<String, String>();
                dispatcherInitParams.put("config", "struts-default.xml," + getConfigPath());
            }
            Dispatcher dispatcher = StrutsTestCaseHelper.initDispatcher(servletContext, dispatcherInitParams);
            configurationManager = dispatcher.getConfigurationManager();
            configuration = configurationManager.getConfiguration();
            container = configuration.getContainer();
            actionProxyFactory = ((ActionProxyFactory) container.getInstance(ActionProxyFactory.class));
        }
        super.configuration = configuration;
        super.configurationManager = configurationManager;
        super.container = container;
        super.httpSession = httpSession;
        super.servletContext = servletContext;
        super.session = session;
        super.actionProxyFactory = actionProxyFactory;
        request = new MockHttpServletRequest(servletContext);
        request.setSession(httpSession);
        container.inject(this);
    }

    @AfterClass
    public static void afterClass() throws Exception {
        StrutsTestCaseHelper.tearDown();
        XWorkTestCaseHelper.tearDown(configurationManager);
        configuration.destroy();
        configuration = null;
        configurationManager = null;
        container = null;
        httpSession = null;
        servletContext = null;
        session = null;
    }

}
