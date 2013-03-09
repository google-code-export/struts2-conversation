package com.github.overengineer.scope;

import java.io.Serializable;

public interface ScopeContainer extends Serializable {
	
	<T> T getComponent(Class<T> clazz);

}
