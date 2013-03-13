package com.google.code.rees.scope.container;

import java.io.Serializable;

public interface ScopeContainerProvider extends Serializable {
	
	ScopeContainer getScopeContainer();

}
