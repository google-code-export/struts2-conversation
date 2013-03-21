package com.github.overengineer.scope.conversation;

import com.github.overengineer.scope.container.BaseModule;
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

public class ConversationModule extends BaseModule {
	
	public ConversationModule() {
		
		resolve(ConversationConfigurationProvider.class).to(DefaultConversationConfigurationProvider.class);
		resolve(ConversationArbitrator.class).to(DefaultConversationArbitrator.class);
		resolve(ConversationProcessor.class).to(InjectionConversationProcessor.class);
		resolve(JeeConversationContextManagerProvider.class).to(DefaultJeeConversationContextManagerProvider.class);
		resolve(ConversationContextFactory.class).to(DefaultConversationContextFactory.class);
		resolve(ConversationContextManager.class).to(TimeoutConversationContextManager.class);
		
		resolve(Properties.CONVERSATION_IDLE_TIMEOUT).to(Defaults.CONVERSATION_IDLE_TIMEOUT);
		resolve(Properties.CONVERSATION_MAX_INSTANCES).to(Defaults.CONVERSATION_MAX_INSTANCES);
		resolve(Properties.CONVERSATION_PACKAGE_NESTING).to(true);
		
	}

}
