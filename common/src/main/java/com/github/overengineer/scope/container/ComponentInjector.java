package com.github.overengineer.scope.container;

import java.lang.reflect.Method;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ComponentInjector<T> implements Injector<T> {
	
	private static final Logger LOG = LoggerFactory.getLogger(ComponentInjector.class);
	
	private Method setter;
	private Class<?> type;
	
	public ComponentInjector(Method setter, Class<?> type) {
		this.setter = setter;
		this.type = type;
	}
	
	public void inject(T component, ScopeContainer container) {
		try {
			setter.invoke(component, container.getComponent(type));
		} catch (Exception e) {
			LOG.info("Could not set singletonComponent of type [{}] on singletonComponent of type [{}] using setter [{}]", type.getName(), component.getClass().getName(), setter.getName());
		}
	}

}
