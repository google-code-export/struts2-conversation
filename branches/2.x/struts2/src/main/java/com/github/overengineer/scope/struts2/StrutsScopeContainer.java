package com.github.overengineer.scope.struts2;

import java.util.HashMap;
import java.util.Map;

import com.github.overengineer.scope.ActionProvider;
import com.github.overengineer.scope.container.AbstractScopeContainer;
import com.github.overengineer.scope.container.ScopeContainer;
import com.github.overengineer.scope.conversation.configuration.ConversationArbitrator;
import com.github.overengineer.scope.conversation.configuration.ConversationConfigurationProvider;
import com.github.overengineer.scope.conversation.context.ConversationContextFactory;
import com.github.overengineer.scope.conversation.context.HttpConversationContextManagerProvider;
import com.github.overengineer.scope.conversation.processing.ConversationProcessor;
import com.github.overengineer.scope.conversation.expression.eval.Eval;
import com.github.overengineer.scope.session.SessionConfigurationProvider;
import com.github.overengineer.scope.session.SessionManager;
import com.github.overengineer.scope.struts2.StrutsScopeConstants.TypeKeys;
import com.opensymphony.xwork2.inject.Container;
import com.opensymphony.xwork2.inject.Inject;

public class StrutsScopeContainer extends AbstractScopeContainer {

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
	
	@Inject(TypeKeys.SESSION_CONFIG_PROVIDER)
	public void setSessionConfigurationProviderKey(String key) {
		typeKeys.put(SessionConfigurationProvider.class, key);
	}
	
	@Inject(TypeKeys.SESSION_MANAGER)
	public void setSessionManagerKey(String key) {
		typeKeys.put(SessionManager.class, key);
	}
	
	@Inject(TypeKeys.EXPRESSION_EVAL)
	public void setExpressionEvalKey(String key) {
		typeKeys.put(Eval.class, key);
	}
	
	@Inject
	public void setContainer(Container container) {
		this.container = container;
	}

	@SuppressWarnings("unchecked")
	@Override
	protected <T> T getPropertyFromPrimaryContainer(Class<T> clazz, String name) {
		String string = container.getInstance(String.class, name);
		if (clazz == long.class) {
			return (T) Long.valueOf(string);
		} else if (clazz == int.class) {
			return (T) Integer.valueOf(string);
		}
		return (T) string;
	}

	@SuppressWarnings("unchecked")
	@Override
	protected <T> T getComponentFromPrimaryContainer(Class<T> clazz) {
		if (ScopeContainer.class.isAssignableFrom(clazz)) {
			return (T) this;
		} else {
			String typeKey = typeKeys.get(clazz);
			if (typeKey == null) {
				return container.getInstance(clazz);
			}
			return container.getInstance(clazz, typeKey);
		}
	}

}
