package com.github.overengineer.scope.conversation;

import com.github.overengineer.container.module.BaseModule;
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

        use(new GenericKey<ScheduledExecutorTimeoutMonitor<ConversationContext>>() {})
                .forType(new GenericKey<TimeoutMonitor<ConversationContext>>() {});

        registerManagedComponentFactory(new GenericKey<Factory<ConversationContextManager>>() {});
        registerNonManagedComponentFactory(ConversationContextFactory.class).toProduce(DefaultConversationContext.class);

        use(Defaults.CONVERSATION_IDLE_TIMEOUT).withName(Properties.CONVERSATION_IDLE_TIMEOUT);
        use(Defaults.CONVERSATION_MAX_INSTANCES).withName(Properties.CONVERSATION_MAX_INSTANCES);
        use(true).withName(Properties.CONVERSATION_PACKAGE_NESTING);
        use(CommonConstants.Defaults.MONITORING_FREQUENCY).withName(CommonConstants.Properties.MONITORING_FREQUENCY);

    }

}
