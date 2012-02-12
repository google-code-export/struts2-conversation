package com.google.code.rees.scope;

import java.io.Serializable;
import java.util.Set;

import com.google.code.rees.scope.conversation.ConversationConfigurationProvider;
import com.google.code.rees.scope.session.SessionConfigurationProvider;

/**
 * Provides a Set of action classes for initializing configurations. In
 * the future, will be employed by the {@link ConversationConfigurationProvider}
 * and the {@link SessionConfigurationProvider}.
 * 
 * @author rees.byars
 */
public interface ActionProvider extends Serializable {

    /**
     * Returns the set of action classes
     * 
     * @return
     */
    public Set<Class<?>> getActionClasses();

}
