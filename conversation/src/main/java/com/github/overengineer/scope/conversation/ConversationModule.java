package com.github.overengineer.scope.conversation;

import com.github.overengineer.container.key.Generic;
import com.github.overengineer.container.module.BaseModule;
import com.github.overengineer.scope.CommonConstants;
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

        use(new Generic<ScheduledExecutorTimeoutMonitor<ConversationContext>>() {})
                .forType(new Generic<TimeoutMonitor<ConversationContext>>() {});

        registerNonManagedComponentFactory(ConversationContextFactory.class).toProduce(DefaultConversationContext.class);

        use(Defaults.CONVERSATION_IDLE_TIMEOUT).withQualifier(Properties.CONVERSATION_IDLE_TIMEOUT);
        use(Defaults.CONVERSATION_MAX_INSTANCES).withQualifier(Properties.CONVERSATION_MAX_INSTANCES);
        use(true).withQualifier(Properties.CONVERSATION_PACKAGE_NESTING);
        use(CommonConstants.Defaults.MONITORING_FREQUENCY).withQualifier(CommonConstants.Properties.MONITORING_FREQUENCY);

    }

}
