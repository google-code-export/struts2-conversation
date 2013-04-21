package com.google.code.rees.scope.container;

import java.io.Serializable;

public interface ScopeContainer extends Serializable {
	
	<T> T getComponent(Class<T> clazz);
	<T> T getProperty(Class<T> clazz, String name);

}
