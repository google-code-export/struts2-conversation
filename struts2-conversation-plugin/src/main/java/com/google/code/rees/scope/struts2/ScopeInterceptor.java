package com.google.code.rees.scope.struts2;

import com.google.code.rees.scope.DefaultScopeManager;
import com.google.code.rees.scope.ScopeAdapterFactory;
import com.google.code.rees.scope.ScopeManager;
import com.google.code.rees.scope.conversation.ConversationArbitrator;
import com.google.code.rees.scope.conversation.ConversationConfigurationProvider;
import com.google.code.rees.scope.conversation.ConversationManager;
import com.google.code.rees.scope.session.SessionConfigurationProvider;
import com.google.code.rees.scope.session.SessionManager;
import com.opensymphony.xwork2.ActionInvocation;
import com.opensymphony.xwork2.inject.Inject;
import com.opensymphony.xwork2.interceptor.Interceptor;
import com.opensymphony.xwork2.util.logging.Logger;
import com.opensymphony.xwork2.util.logging.LoggerFactory;

public class ScopeInterceptor implements Interceptor {

	private static final long serialVersionUID = 3222190171260674636L;
	private static final Logger LOG = LoggerFactory
			.getLogger(ScopeInterceptor.class);

	protected ScopeManager manager = new DefaultScopeManager();
	protected ScopeAdapterFactory adapterFactory = new StrutsScopeAdapterFactory();
	protected ConversationArbitrator arbitrator;
	protected ConversationManager conversationManager;
	protected ConversationConfigurationProvider conversationConfigurationProvider;
	protected SessionManager sessionManager;
	protected SessionConfigurationProvider sessionConfigurationProvider;
	protected ActionFinder finder;
	protected String actionSuffix;

	@Inject(StrutsScopeConstants.ACTION_FINDER_KEY)
	public void setActionClassFinder(ActionFinder finder) {
		this.finder = finder;
	}
	
	@Inject(ConventionConstants.ACTION_SUFFIX)
	public void setActionSuffix(String suffix) {
		this.actionSuffix = suffix;
	}
	
	@Inject(value = StrutsScopeConstants.SCOPE_MANAGER_KEY, required = false)
	public void setScopeManager(ScopeManager manager) {
		this.manager = manager;
	}
	
	@Inject(value = StrutsScopeConstants.CONVERSATION_MANAGER_KEY, required = false)
	public void setConversationManager(ConversationManager manager) {
		this.conversationManager = manager;
	}
	
	@Inject(value = StrutsScopeConstants.CONVERSATION_CONFIG_PROVIDER_KEY, required = false)
	public void setConversationConfigurationProvider(
			ConversationConfigurationProvider conversationConfigurationProvider) {
		this.conversationConfigurationProvider = conversationConfigurationProvider;
	}
	
	@Inject(value = StrutsScopeConstants.SESSION_MANAGER_KEY, required = false)
	public void setSessionManager(SessionManager manager) {
		this.sessionManager = manager;
	}

	@Inject(value = StrutsScopeConstants.SESSION_CONFIG_PROVIDER_KEY, required = false)
	public void setSessionConfigurationProvider(
			SessionConfigurationProvider sessionConfigurationProvider) {
		this.sessionConfigurationProvider = sessionConfigurationProvider;
	}
	
	@Inject(value = StrutsScopeConstants.SCOPE_ADAPTER_FACTORY_KEY, required = false)
	public void setConversationAdapterFactory(StrutsScopeAdapterFactory adapterFactory) {
		this.adapterFactory = adapterFactory;
	}

	@Override
	public void destroy() {
		LOG.info("Destroying the ScopeInterceptor...");
	}

	@Override
	public void init() {
		
		LOG.info("Initializing the ScopeInterceptor...");
		
		if (this.conversationConfigurationProvider != null) {
			this.manager.setConversationConfigurationProvider(conversationConfigurationProvider);
		}
		
		if (this.conversationManager != null) {
			this.manager.setConversationManager(conversationManager);
		}
		
		if (this.sessionConfigurationProvider != null) {
			this.manager.setSessionConfigurationProvider(sessionConfigurationProvider);
		}
		
		if (this.sessionManager != null) {
			this.manager.setSessionManager(sessionManager);
		}
		
		if (this.arbitrator != null) {
			this.arbitrator.setActionSuffix(actionSuffix);
			this.manager.setConversationArbitrator(this.arbitrator);
		}
		
		this.manager.init(this.adapterFactory, this.finder.getActionClasses());
	}

	@Override
	public String intercept(ActionInvocation invocation) throws Exception {
		this.manager.processScopes();
		return invocation.invoke();
	}

}
