package com.google.code.rees.scope;

import java.io.Serializable;
import java.util.Set;

public interface ActionProvider extends Serializable {

	public Set<Class<?>> getActionClasses();
	
}
