package com.google.code.rees.scope.struts2;

import com.google.code.rees.scope.conversation.ConversationConfigBuilder;
import com.google.code.rees.scope.conversation.ConversationManager;
import com.google.code.rees.scope.session.SessionFieldConfigBuilder;
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

	protected ConversationManager conversationManager;
	protected SessionManager sessionManager;
	protected ConversationConfigBuilder conversationConfigBuilder;
	protected SessionFieldConfigBuilder sessionFieldConfigBuilder;

	@Inject(StrutsScopeConstants.CONVERSATION_CONFIG_BUILDER_KEY)
	public void setConversationConfigBuilder(
			ConversationConfigBuilder configBuilder) {
		this.conversationConfigBuilder = configBuilder;
	}
	
	@Inject(StrutsScopeConstants.CONVERSATION_MANAGER_KEY)
	public void setConversationManager(ConversationManager manager) {
		this.conversationManager = manager;
	}
	
	@Inject(StrutsScopeConstants.SESSION_MANAGER_KEY)
	public void setSessionManager(SessionManager manager) {
		this.sessionManager = manager;
	}

	@Inject(StrutsScopeConstants.SESSION_FIELD_CONFIG_BUILDER_KEY)
	public void setSessionFieldConfigBuilder(
			SessionFieldConfigBuilder configBuilder) {
		this.sessionFieldConfigBuilder = configBuilder;
	}

	@Override
	public void destroy() {
		LOG.info("Destroying the ScopeInterceptor...");
	}

	@Override
	public void init() {
		LOG.info("Initializing the ScopeInterceptor...");
		conversationManager.setConversationConfigBuilder(conversationConfigBuilder);
		sessionManager.setSessionFieldConfigBuilder(sessionFieldConfigBuilder);
	}

	@Override
	public String intercept(ActionInvocation invocation) throws Exception {
		sessionManager.processSessionFields(new StrutsSessionAdapter(invocation));
		conversationManager.processConversations(new StrutsConversationAdapter(invocation));
		return invocation.invoke();
	}

}
