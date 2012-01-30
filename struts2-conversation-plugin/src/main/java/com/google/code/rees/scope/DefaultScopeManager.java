package com.google.code.rees.scope;

import java.util.HashSet;
import java.util.Set;

import com.google.code.rees.scope.conversation.ConversationArbitrator;
import com.google.code.rees.scope.conversation.ConversationConfigurationProvider;
import com.google.code.rees.scope.conversation.ConversationManager;
import com.google.code.rees.scope.conversation.DefaultConversationArbitrator;
import com.google.code.rees.scope.conversation.DefaultConversationConfigurationProvider;
import com.google.code.rees.scope.conversation.DefaultConversationManager;
import com.google.code.rees.scope.session.DefaultSessionConfigurationProvider;
import com.google.code.rees.scope.session.DefaultSessionManager;
import com.google.code.rees.scope.session.SessionConfigurationProvider;
import com.google.code.rees.scope.session.SessionManager;

public class DefaultScopeManager implements ScopeManager {
	
	private static final long serialVersionUID = -7042031513311747101L;
	
	protected SessionManager sessionManager = new DefaultSessionManager();
	protected SessionConfigurationProvider sessionConfigurationProvider = new DefaultSessionConfigurationProvider();
	protected ConversationArbitrator arbitrator = new DefaultConversationArbitrator();
	protected ConversationManager conversationManager = new DefaultConversationManager();
	protected ConversationConfigurationProvider conversationConfigurationProvider = new DefaultConversationConfigurationProvider();
	protected ScopeAdapterFactory adapterFactory;
	
	@Override
	public void setConversationArbitrator(ConversationArbitrator arbitrator) {
		this.arbitrator = arbitrator;
	}
	
	@Override
	public void setConversationManager(ConversationManager manager) {
		this.conversationManager = manager;
	}
	
	@Override
	public void setConversationConfigurationProvider(
			ConversationConfigurationProvider conversationConfigurationProvider) {
		this.conversationConfigurationProvider = conversationConfigurationProvider;
	}
	
	@Override
	public void setSessionManager(SessionManager manager) {
		this.sessionManager = manager;
	}

	@Override
	public void setSessionConfigurationProvider(
			SessionConfigurationProvider sessionConfigurationProvider) {
		this.sessionConfigurationProvider = sessionConfigurationProvider;
	}

	@Override
	public void init(ScopeAdapterFactory adapterFactory) {
		this.init(adapterFactory, new HashSet<Class<?>>());
	}

	@Override
	public void init(ScopeAdapterFactory adapterFactory, Set<Class<?>> classes) {
		this.adapterFactory = adapterFactory;
		this.conversationConfigurationProvider.init(this.arbitrator, classes);
		this.sessionConfigurationProvider.init(classes);
		this.conversationManager.init(this.conversationConfigurationProvider);
		this.sessionManager.init(this.sessionConfigurationProvider);
	}
	
	@Override
	public void processScopes() {
		this.sessionManager.processSessionFields(this.adapterFactory.createSessionAdapter());
		this.conversationManager.processConversations(this.adapterFactory.createConversationAdapter());
	}

}
