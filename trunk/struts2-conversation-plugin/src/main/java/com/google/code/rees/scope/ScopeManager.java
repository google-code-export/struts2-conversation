package com.google.code.rees.scope;

import java.io.Serializable;
import java.util.Set;

import com.google.code.rees.scope.conversation.ConversationArbitrator;
import com.google.code.rees.scope.conversation.ConversationConfigurationProvider;
import com.google.code.rees.scope.conversation.ConversationManager;
import com.google.code.rees.scope.session.SessionConfigurationProvider;
import com.google.code.rees.scope.session.SessionManager;

public interface ScopeManager extends Serializable {
	public void setConversationArbitrator(ConversationArbitrator arbitrator);
	public void setConversationManager(ConversationManager manager);
	public void setConversationConfigurationProvider(ConversationConfigurationProvider conversationConfigurationProvider);
	public void setSessionManager(SessionManager manager);
	public void setSessionConfigurationProvider(SessionConfigurationProvider sessionConfigurationProvider);
	public void init(ScopeAdapterFactory adapterFactory);
	public void init(ScopeAdapterFactory adapterFactory, Set<Class<?>> classes);
	public void processScopes();
}
