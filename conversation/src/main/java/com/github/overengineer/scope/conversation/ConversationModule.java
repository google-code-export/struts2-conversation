package com.github.overengineer.scope.conversation;

import com.github.overengineer.scope.container.AbstractModule;
import com.github.overengineer.scope.conversation.ConversationConstants.Defaults;
import com.github.overengineer.scope.conversation.ConversationConstants.Properties;
import com.github.overengineer.scope.conversation.configuration.ConversationArbitrator;
import com.github.overengineer.scope.conversation.configuration.ConversationConfigurationProvider;
import com.github.overengineer.scope.conversation.configuration.DefaultConversationArbitrator;
import com.github.overengineer.scope.conversation.configuration.DefaultConversationConfigurationProvider;
import com.github.overengineer.scope.conversation.context.ConversationContextFactory;
import com.github.overengineer.scope.conversation.context.ConversationContextManager;
import com.github.overengineer.scope.conversation.context.DefaultConversationContextFactory;
import com.github.overengineer.scope.conversation.context.DefaultJeeConversationContextManagerProvider;
import com.github.overengineer.scope.conversation.context.JeeConversationContextManagerProvider;
import com.github.overengineer.scope.conversation.context.TimeoutConversationContextManager;
import com.github.overengineer.scope.conversation.processing.ConversationProcessor;
import com.github.overengineer.scope.conversation.processing.InjectionConversationProcessor;

public class ConversationModule extends AbstractModule {
	
	public ConversationModule() {
		
		setComponent(ConversationConfigurationProvider.class, DefaultConversationConfigurationProvider.class);
		setComponent(ConversationArbitrator.class, DefaultConversationArbitrator.class);
		setComponent(ConversationProcessor.class, InjectionConversationProcessor.class);
		setComponent(JeeConversationContextManagerProvider.class, DefaultJeeConversationContextManagerProvider.class);
		setComponent(ConversationContextFactory.class, DefaultConversationContextFactory.class);
		setComponent(ConversationContextManager.class, TimeoutConversationContextManager.class);
		
		setProperty(Properties.CONVERSATION_IDLE_TIMEOUT, Defaults.CONVERSATION_IDLE_TIMEOUT);
		setProperty(Properties.CONVERSATION_MAX_INSTANCES, Defaults.CONVERSATION_MAX_INSTANCES);
		setProperty(Properties.CONVERSATION_PACKAGE_NESTING, true);
		
	}

}
