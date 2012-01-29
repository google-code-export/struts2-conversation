package com.google.code.rees.scope.struts2;

import java.io.Serializable;
import java.util.Set;

public interface ActionFinder extends Serializable {

	public Set<Class<?>> getActionClasses();
	
}
