package com.google.code.rees.scope.conversation;

import java.io.Serializable;
import java.util.Collection;
import java.util.Set;

/**
 * This class is used by {@link ConversationManager ConversationManagers} to
 * obtain the {@link ConversationConfiguration ConversationConfigurations} for a
 * given action/controller class
 * 
 * @author rees.byars
 * 
 */
public interface ConversationConfigurationProvider extends Serializable {

    /**
     * Set the {@link ConversationArbitrator} to be used for building the
     * configurations
     * 
     * @param arbitrator
     */
    public void setArbitrator(ConversationArbitrator arbitrator);

    /**
     * Initialize the configuration caches for a given set of classes
     * 
     * @param actionClasses
     */
    public void init(Set<Class<?>> actionClasses);

    /**
     * Get the {@link ConversationConfiguration ConversationConfigurations} for
     * a given class
     * 
     * @param actionClass
     * @return
     */
    public Collection<ConversationConfiguration> getConfigurations(
            Class<?> actionClass);

}
