package com.google.code.rees.scope.session;

import java.io.Serializable;
import java.util.Set;

/**
 * This class is used by {@link SessionManager SessionManagers} to
 * obtain the {@link SessionConfiguration}
 * 
 * @author rees.byars
 */
public interface SessionConfigurationProvider extends Serializable {

    /**
     * Initialize the configuration for a given set of classes
     * 
     * @param actionClasses
     */
    public void init(Set<Class<?>> classes);

    /**
     * Get the current configuration, adding the given class to
     * the configuration if not already included
     * 
     * @param clazz
     * @return
     */
    public SessionConfiguration getSessionConfiguration(Class<?> clazz);

}
