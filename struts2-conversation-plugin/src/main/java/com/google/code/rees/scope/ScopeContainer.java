package com.google.code.rees.scope;

import java.io.Serializable;

public interface ScopeContainer extends Serializable {
	
	<T> T getComponent(Class<T> clazz);

}
