package com.github.overengineer.scope.container;

import java.lang.reflect.Method;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PropertyInjector<T> implements Injector<T> {
	
	private static final Logger LOG = LoggerFactory.getLogger(PropertyInjector.class);
	
	private Method setter;
	private String name;
	private Class<?> type;
	
	public PropertyInjector(Method setter, String name, Class<?> type) {
		this.setter = setter;
		this.name = name;
		this.type = type;
	}
	
	public void inject(T component, ScopeContainer container) {
		try {
			Object value = container.getProperty(type, name);
			LOG.debug("Setting property [{}] on component of type [{}] with value [{}]", name, component.getClass().getName(), value);
			setter.invoke(component, value);
		} catch (Exception e) {
			LOG.error("Could not set property [{}] of type [{}] on component of type [{}] using setter [{}]", name, type.getName(), component.getClass().getName(), setter.getName(), e);
		}
	}

}
