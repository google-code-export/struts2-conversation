package com.google.code.rees.scope.conversation;

import java.io.Serializable;
import java.util.Collection;
import java.util.Set;

public interface ConversationConfigurationProvider extends Serializable {

    public void setArbitrator(ConversationArbitrator arbitrator);

    public void init(Set<Class<?>> actionClasses);

    public Collection<ConversationConfiguration> getConfigurations(
            Class<?> actionClass);

}
