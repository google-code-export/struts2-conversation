package com.github.overengineer.scope.conversation;

import com.github.overengineer.container.BaseModule;
import com.github.overengineer.container.key.GenericKey;
import com.github.overengineer.scope.CommonConstants;
import com.github.overengineer.scope.Factory;
import com.github.overengineer.scope.conversation.ConversationConstants.Defaults;
import com.github.overengineer.scope.conversation.ConversationConstants.Properties;
import com.github.overengineer.scope.conversation.configuration.ConversationArbitrator;
import com.github.overengineer.scope.conversation.configuration.ConversationConfigurationProvider;
import com.github.overengineer.scope.conversation.configuration.DefaultConversationArbitrator;
import com.github.overengineer.scope.conversation.configuration.DefaultConversationConfigurationProvider;
import com.github.overengineer.scope.conversation.context.*;
import com.github.overengineer.scope.conversation.processing.ConversationProcessor;
import com.github.overengineer.scope.conversation.processing.InjectionConversationProcessor;
import com.github.overengineer.scope.monitor.ScheduledExecutorTimeoutMonitor;
import com.github.overengineer.scope.monitor.TimeoutMonitor;

public class ConversationModule extends BaseModule {

    @Override
    protected void configure() {

        use(DefaultConversationConfigurationProvider.class).forType(ConversationConfigurationProvider.class);
        use(DefaultConversationArbitrator.class).forType(ConversationArbitrator.class);
        use(InjectionConversationProcessor.class).forType(ConversationProcessor.class);
        use(DefaultJeeConversationContextManagerProvider.class).forType(JeeConversationContextManagerProvider.class);
        use(TimeoutConversationContextManager.class).forType(ConversationContextManager.class);

        use(ScheduledExecutorTimeoutMonitor.class).forGeneric(new GenericKey<TimeoutMonitor<ConversationContext>>() {});

        registerManagedComponentFactory(new GenericKey<Factory<ConversationContextManager>>() {});
        registerNonManagedComponentFactory(ConversationContextFactory.class).toProduce(DefaultConversationContext.class);

        set(Properties.CONVERSATION_IDLE_TIMEOUT).to(Defaults.CONVERSATION_IDLE_TIMEOUT);
        set(Properties.CONVERSATION_MAX_INSTANCES).to(Defaults.CONVERSATION_MAX_INSTANCES);
        set(Properties.CONVERSATION_PACKAGE_NESTING).to(true);
        set(CommonConstants.Properties.MONITORING_FREQUENCY).to(CommonConstants.Defaults.MONITORING_FREQUENCY);

    }

}
