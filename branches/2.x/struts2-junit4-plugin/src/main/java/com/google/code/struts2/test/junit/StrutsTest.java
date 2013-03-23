package com.google.code.struts2.test.junit;

import static org.junit.Assert.assertNotNull;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import javax.activation.MimetypesFileTypeMap;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang3.StringUtils;
import org.apache.struts2.ServletActionContext;
import org.apache.struts2.dispatcher.Dispatcher;
import org.apache.struts2.dispatcher.SessionMap;
import org.apache.struts2.dispatcher.mapper.ActionMapper;
import org.apache.struts2.dispatcher.mapper.ActionMapping;
import org.apache.struts2.util.TokenHelper;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.rules.TestName;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.mock.web.MockPageContext;
import org.springframework.mock.web.MockServletContext;

import com.google.code.struts2.test.ProxyExecutionListener;
import com.google.code.struts2.test.TestActionProxy;
import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.ActionInvocation;
import com.opensymphony.xwork2.ActionProxy;
import com.opensymphony.xwork2.ActionProxyFactory;
import com.opensymphony.xwork2.ValidationAware;
import com.opensymphony.xwork2.config.Configuration;
import com.opensymphony.xwork2.config.ConfigurationManager;
import com.opensymphony.xwork2.inject.Container;
import com.opensymphony.xwork2.interceptor.PreResultListener;
import com.opensymphony.xwork2.util.ValueStack;
import com.opensymphony.xwork2.util.ValueStackFactory;

/**
 * some of this code borrowed from struts2 junit plugin
 */
public abstract class StrutsTest<T> implements ProxyExecutionListener, PreResultListener {

    /**
     * The token used for the
     * {@link org.apache.struts2.interceptor.TokenInterceptor TokenInterceptor}
     * and/or a
     * {@link org.apache.struts2.interceptor.TokenSessionStoreInterceptor
     * TokenSessionStoreInterceptor}.
     */
    public static final String TEST_TOKEN = "test.token";

    @Rule
    public TestName currentlyExecutingTest = new TestName();
    private T actionFromProxy;
    protected ConfigurationManager configurationManager;
    protected Configuration configuration;
    protected Container container;
    protected ActionProxyFactory actionProxyFactory;
    protected String strutsConfigs;
    protected MockHttpServletResponse response;
    protected MockHttpServletRequest request;
    protected MockPageContext pageContext;
    protected MockServletContext servletContext;
    protected Map<String, String> dispatcherInitParams;
    protected HttpSession httpSession;
    protected Map<String, Object> session;
    protected DefaultResourceLoader resourceLoader = new DefaultResourceLoader();

    /**
     * gets an object from the stack after an action is executed
     */
    protected Object findValueAfterExecute(String key) {
        return ServletActionContext.getValueStack(request).findValue(key);
    }

    /**
     * @return The action object associated with the proxy
     */
    protected T getAction() {
        return this.actionFromProxy;
    }

    @SuppressWarnings("unchecked")
    protected <U> U getAction(Class<U> clazz) {
        return (U) this.actionFromProxy;
    }

    protected boolean containsErrors() {
        Object action = this.getAction();
        if (action instanceof ValidationAware) {
            return ((ValidationAware) action).hasActionErrors();
        }
        throw new UnsupportedOperationException("Current action does not implement ValidationAware interface");
    }

    /**
     * Executes an action and returns it's output (not the result returned from
     * execute()), but the actual output that would be written to the response.
     * For this to work the configured result for the action needs to be
     * FreeMarker, or Velocity (JSPs can be used with the Embedded JSP plugin)
     */
    protected String executeAction(String uri) throws ServletException, UnsupportedEncodingException {
        request.setRequestURI(uri);
        ActionMapping mapping = getActionMapping(request);

        assertNotNull(mapping);
        Dispatcher.getInstance().serviceAction(request, response, servletContext, mapping);

        if (response.getStatus() != HttpServletResponse.SC_OK)
            throw new ServletException("Error code [" + response.getStatus() + "], Error: [" + response.getErrorMessage() + "]");

        return response.getContentAsString();
    }

    /**
     * Creates an action proxy for a request, and sets parameters of the
     * ActionInvocation to the passed parameters. Make sure to set the request
     * parameters in the protected "request" object before calling this method.
     */
    protected ActionProxy getActionProxy(String uri) {

        preProxySetup();

        ActionProxy proxy = getProxy(uri);

        return proxy;
    }

    protected ActionProxy getActionProxy(String multipartName, File file, String actionUri) {
        preProxySetup();
        ActionProxy proxy = this.getProxy(actionUri);
        proxy.getInvocation().getInvocationContext().setParameters(buildMultipartParams(multipartName, file));
        return proxy;
    }

    protected ActionProxy getTokenReadyActionProxy(String multipartName, File file, String actionUri) {
        this.preProxySetup();
        this.setTokens();
        ActionProxy proxy = this.getProxy(actionUri);
        proxy.getInvocation().getInvocationContext().setParameters(buildMultipartParams(multipartName, file));
        return proxy;
    }

    protected Map<String, Object> buildMultipartParams(String multipartName, File file) {

        Map<String, Object> params = new HashMap<String, Object>();
        params.putAll(request.getParameterMap());
        params.put(multipartName, file);
        params.put(multipartName + "FileName", file.getName());
        params.put(multipartName + "ContentType", MimetypesFileTypeMap.getDefaultFileTypeMap().getContentType(file));
        return params;
    }

    @SuppressWarnings("unchecked")
    private ActionProxy getProxy(String uri) {
        ServletActionContext.setRequest(request);
        request.setRequestURI(uri);
        ActionMapping mapping = getActionMapping(request);
        String namespace = mapping.getNamespace();
        String name = mapping.getName();
        String method = mapping.getMethod();

        ActionProxy proxy = this.actionProxyFactory.createActionProxy(namespace, name, method, new HashMap<String, Object>(), true, false);
        TestActionProxy testProxy = new TestActionProxy(proxy);
        testProxy.addExecutionListener(this);
        this.actionFromProxy = (T) proxy.getAction();
        proxy.setExecuteResult(isExecuteResult());
        ActionContext invocationContext = proxy.getInvocation().getInvocationContext();
        invocationContext.setParameters(new HashMap<String, Object>(request.getParameterMap()));
        invocationContext.setSession(session);
        // set the action context to the one used by the proxy
        ActionContext.setContext(invocationContext);
        ServletActionContext.setServletContext(servletContext);
        ServletActionContext.setRequest(request);
        ServletActionContext.setResponse(response);

        proxy.getInvocation().addPreResultListener(this);

        return testProxy;
    }

    /**
     * Uses {@link #getActionProxy(String)} and additional request and session
     * parameters to obtain an ActionProxy that will pass validation by a
     * {@link org.apache.struts2.interceptor.TokenInterceptor TokenInterceptor}
     * and/or a
     * {@link org.apache.struts2.interceptor.TokenSessionStoreInterceptor
     * TokenSessionStoreInterceptor}. <br>
     * <br>
     * <b>Note:</b> Sets token and token name params on request.
     *
     * @param actionPath The path to the action
     * @return An ActionProxy with valid tokens in its session map
     */
    protected ActionProxy getTokenReadyActionProxy(String uri) {
        this.preProxySetup();
        this.setTokens();
        ActionProxy proxy = getProxy(uri);
        return proxy;
    }

    private void setTokens() {

        /*
         * Add token name and token to request
         */
        request.addParameter(TokenHelper.TOKEN_NAME_FIELD, TokenHelper.DEFAULT_TOKEN_NAME);
        request.addParameter(TokenHelper.DEFAULT_TOKEN_NAME, TEST_TOKEN);

        /*
         * Create session map, add token, and set on proxy context
         */
        session.put(TokenHelper.DEFAULT_TOKEN_NAME, TEST_TOKEN);

    }

    /**
     * Finds an ActionMapping for a given request
     */
    protected ActionMapping getActionMapping(HttpServletRequest request) {
        return container.getInstance(ActionMapper.class).getMapping(request, configurationManager);
    }

    /**
     * Finds an ActionMapping for a given url
     */
    protected ActionMapping getActionMapping(String url) {
        MockHttpServletRequest req = new MockHttpServletRequest();
        req.setRequestURI(url);
        return getActionMapping(req);
    }

    /**
     * Injects dependencies on an Object using Struts internal IoC container
     */
    protected void injectStrutsDependencies(Object object) {
        Dispatcher.getInstance().getContainer().inject(object);
    }

    protected void setupBeforeInitDispatcher() throws Exception {
    }

    protected void initServletMockObjects() {
        servletContext = new MockServletContext(resourceLoader);
        httpSession = new MockHttpSession(servletContext);
        response = new MockHttpServletResponse();
        request = new MockHttpServletRequest(servletContext);
        request.setSession(httpSession);
        pageContext = new MockPageContext(servletContext, request, response);
        session = new SessionMap<String, Object>(request);
    }

    protected void preProxySetup() {
        response = new MockHttpServletResponse();
        pageContext = new MockPageContext(servletContext, request, response);
    }

    /**
     * Sets up the configuration settings, XWork configuration, and message
     * resources
     */
    @Before
    public void setUp() throws Exception {
        processAnnotations(currentlyExecutingTest.getMethodName());
        initServletMockObjects();
        setupBeforeInitDispatcher();
        initDispatcherParams();
        initDispatcher(dispatcherInitParams);
        actionProxyFactory = ((ActionProxyFactory) container.getInstance(ActionProxyFactory.class));
        injectStrutsDependencies(this);
    }

    protected void initDispatcherParams() {
        if (StringUtils.isNotBlank(getConfigPath())) {
            dispatcherInitParams = new HashMap<String, String>();
            dispatcherInitParams.put("config", "struts-default.xml," + getConfigPath());
        }
    }

    protected Dispatcher initDispatcher(Map<String, String> params) {
        Dispatcher du = new Dispatcher(servletContext, params);
        du.init();
        Dispatcher.setInstance(du);
        ValueStack stack = ((ValueStackFactory) du.getContainer().getInstance(ValueStackFactory.class)).createValueStack();
        stack.getContext().put("com.opensymphony.xwork2.ActionContext.container", du.getContainer());
        ActionContext.setContext(new ActionContext(stack.getContext()));
        configurationManager = du.getConfigurationManager();
        configuration = configurationManager.getConfiguration();
        container = configuration.getContainer();
        return du;
    }

    /**
     * Called during dispatcher initialization to obtain a comma-separated list
     * of struts config files to load.
     * <p/>
     * If the {@link @StrutsConfiguration} annotation is not used, then this
     * method returns <code>"struts-plugin.xml,struts.xml"</code> by default.
     * <p/>
     * If the {@link @StrutsConfiguration} annotation <i>is</i> used, then this
     * method will return a String in the format <code>("struts-plugin.xml," +
     * class-level {@link @StrutsConfiguration} + method-level {@link @StrutsConfiguration})</code>.
     *
     * @return a comma separated list of config locations
     */
    protected String getConfigPath() {
        if (strutsConfigs.equals("")) {
            strutsConfigs = "struts.xml";
        }
        return "struts-plugin.xml," + strutsConfigs;
    }

    @After
    public void tearDown() throws Exception {
        if (configurationManager != null) {
            configurationManager.destroyConfiguration();
            configurationManager = null;
        }
        ActionContext.setContext(null);
        this.configurationManager = null;
        this.configuration = null;
        this.container = null;
        this.actionProxyFactory = null;
        Dispatcher.setInstance(null);
        session = null;
        httpSession = null;
    }

    /**
     * this can be overridden to change the default of the action proxy
     * returned by getActionProxy
     *
     * @return
     */
    protected boolean isExecuteResult() {
        return true;
    }

    protected void processAnnotations(String methodName) throws Exception {
        strutsConfigs = "";
        if (this.getClass().isAnnotationPresent(StrutsConfiguration.class)) {
            StrutsConfiguration classLevelStrutsConfig = this.getClass().getAnnotation(StrutsConfiguration.class);
            strutsConfigs += StringUtil.unsplit(",", classLevelStrutsConfig.locations());
            strutsConfigs += ",";
        }
        Method method = this.getClass().getMethod(methodName);
        if (method.isAnnotationPresent(StrutsConfiguration.class)) {
            StrutsConfiguration strutsConfig = method.getAnnotation(StrutsConfiguration.class);
            strutsConfigs += StringUtil.unsplit(",", strutsConfig.locations());
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void afterProxyExecution(ActionInvocation invocation, String result) {
        request = new MockHttpServletRequest(servletContext);
        request.setSession(httpSession);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void beforeResult(ActionInvocation invocation, String resultCode) {

    }

}
