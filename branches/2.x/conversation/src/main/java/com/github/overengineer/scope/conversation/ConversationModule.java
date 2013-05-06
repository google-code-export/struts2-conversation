package com.github.overengineer.scope.conversation;

import com.github.overengineer.container.BaseModule;
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

        use(DefaultConversationConfigurationProvider.class).forType(ConversationConfigurationProvider.class);
        use(DefaultConversationArbitrator.class).forType(ConversationArbitrator.class);
        use(InjectionConversationProcessor.class).forType(ConversationProcessor.class);
        use(DefaultJeeConversationContextManagerProvider.class).forType(JeeConversationContextManagerProvider.class);
        use(DefaultConversationContextFactory.class).forType(ConversationContextFactory.class);
        use(TimeoutConversationContextManager.class).forType(ConversationContextManager.class);

        set(Properties.CONVERSATION_IDLE_TIMEOUT).to(Defaults.CONVERSATION_IDLE_TIMEOUT);
        set(Properties.CONVERSATION_MAX_INSTANCES).to(Defaults.CONVERSATION_MAX_INSTANCES);
        set(Properties.CONVERSATION_PACKAGE_NESTING).to(true);

    }

}
