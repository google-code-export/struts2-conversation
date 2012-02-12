package com.google.code.rees.scope;

import java.io.Serializable;
import java.util.Set;

/**
 * 
 * @author rees.byars
 * 
 */
public interface ActionProvider extends Serializable {

    public Set<Class<?>> getActionClasses();

}
