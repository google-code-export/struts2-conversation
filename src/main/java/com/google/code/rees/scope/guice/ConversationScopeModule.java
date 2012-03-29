package com.google.code.rees.scope.guice;

import com.google.inject.AbstractModule;

public class ConversationScopeModule extends AbstractModule {

    @Override
    protected void configure() {
        final ConversationScope scope = new ConversationScope();
        this.bindScope(ConversationScoped.class, scope);
        this.bind(ConversationScope.class)
                .annotatedWith(ConversationScoped.class).toInstance(scope);
    }

}
