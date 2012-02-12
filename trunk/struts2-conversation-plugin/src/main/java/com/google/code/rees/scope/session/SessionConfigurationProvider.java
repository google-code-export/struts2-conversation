package com.google.code.rees.scope.session;

import java.io.Serializable;
import java.util.Set;

/**
 * 
 * @author rees.byars
 * 
 */
public interface SessionConfigurationProvider extends Serializable {

    public void init(Set<Class<?>> classes);

    public SessionConfiguration getSessionConfiguration(Class<?> clazz);

}
