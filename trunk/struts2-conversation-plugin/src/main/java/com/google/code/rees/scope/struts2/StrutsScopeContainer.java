package com.google.code.rees.scope.struts2;

import java.util.HashMap;
import java.util.Map;

import com.google.code.rees.scope.ActionProvider;
import com.google.code.rees.scope.ScopeContainer;
import com.google.code.rees.scope.conversation.ConversationProperties;
import com.google.code.rees.scope.conversation.configuration.ConversationArbitrator;
import com.google.code.rees.scope.conversation.configuration.ConversationConfigurationProvider;
import com.google.code.rees.scope.conversation.context.ConversationContextFactory;
import com.google.code.rees.scope.conversation.context.HttpConversationContextManagerProvider;
import com.google.code.rees.scope.conversation.processing.ConversationProcessor;
import com.google.code.rees.scope.session.SessionConfigurationProvider;
import com.google.code.rees.scope.session.SessionManager;
import com.google.code.rees.scope.struts2.StrutsScopeConstants.TypeKeys;
import com.opensymphony.xwork2.inject.Container;
import com.opensymphony.xwork2.inject.Inject;

public class StrutsScopeContainer implements ScopeContainer {

	private static final long serialVersionUID = -6820777796732236492L;
	
	private Map<Class<?>, String> typeKeys  = new HashMap<Class<?>, String>();
	private Container container;
	
	@Inject(TypeKeys.ACTION_PROVIDER)
	public void setActionProviderKey(String key) {
		typeKeys.put(ActionProvider.class, key);
	}
	
	@Inject(TypeKeys.CONVERSATION_ARBITRATOR)
	public void setConversationArbitratorKey(String key) {
		typeKeys.put(ConversationArbitrator.class, key);
	}
	
	@Inject(TypeKeys.CONVERSATION_CONFIG_PROVIDER)
	public void setConversationConfigurationProviderKey(String key) {
		typeKeys.put(ConversationConfigurationProvider.class, key);
	}
	
	@Inject(TypeKeys.CONVERSATION_CONTEXT_FACTORY)
	public void setConversationContextFactoryKey(String key) {
		typeKeys.put(ConversationContextFactory.class, key);
	}
	
	@Inject(TypeKeys.CONVERSATION_CONTEXT_MANAGER_PROVIDER)
	public void setConversationContextManagerProviderKey(String key) {
		typeKeys.put(HttpConversationContextManagerProvider.class, key);
	}
	
	@Inject(TypeKeys.CONVERSATION_PROCESSOR)
	public void setConversationProcessorKey(String key) {
		typeKeys.put(ConversationProcessor.class, key);
	}
	
	@Inject(TypeKeys.CONVERSATION_PROPERTIES)
	public void setConversationPropertiesKey(String key) {
		typeKeys.put(ConversationProperties.class, key);
	}
	
	@Inject(TypeKeys.SESSION_CONFIG_PROVIDER)
	public void setSessionConfigurationProviderKey(String key) {
		typeKeys.put(SessionConfigurationProvider.class, key);
	}
	
	@Inject(TypeKeys.SESSION_MANAGER)
	public void setSessionManagerKey(String key) {
		typeKeys.put(SessionManager.class, key);
	}
	
	@Inject
	public void setContainer(Container container) {
		this.container = container;
	}

	@Override
	public <T> T getComponent(Class<T> clazz) {
		return container.getInstance(clazz, typeKeys.get(clazz));
	}

}
