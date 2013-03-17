package com.github.overengineer.scope.container;

import java.util.HashMap;
import java.util.Map;

public class AbstractModule implements Module {
	
	private Map<Class<?>, Class<?>> components = new HashMap<Class<?>, Class<?>>();
	private Map<String, Object> properties = new HashMap<String, Object>();

	@Override
	public final Map<Class<?>, Class<?>> getComponentMappings() {
		return components;
	}
	
	@Override
	public final Map<String, Object> getProperties() {
		return properties;
	}
	
	public final <T> void setComponent(Class<T> componentType, Class<? extends T> implementationType) {
		components.put(componentType, implementationType);
	}
	
	public final <T> void setProperty(String name, Object value) {
		properties.put(name, value);
	}
	
}
