package com.github.overengineer.scope.container;

import java.util.HashMap;
import java.util.Map;

public class BaseModule implements Module {
	
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
	
	public <K> ClassMapper<K> resolve(Class<K> componentClass) {
		return new ClassMapper<K>(componentClass, components);
	}
	
	public PropertyMapper resolve(String name) {
		return new PropertyMapper(name, properties);
	}
	
	public interface Mapper<T> {
		public void to(T value);
	}
	
	public static class ClassMapper<K> implements Mapper<Class<? extends K>> {
		private Class<K> key;
		private Map<Class<?>, Class<?>> map;
		public ClassMapper(Class<K> key, Map<Class<?>, Class<?>> map) {
			this.key = key;
			this.map = map;
		}
		@Override
		public void to(Class<? extends K> value) {
			map.put(key, value);
		}
	}
	
	public static class PropertyMapper implements Mapper<Object> {
		private String key;
		private Map<String, Object> map;
		public PropertyMapper(String key, Map<String, Object> map) {
			this.key = key;
			this.map = map;
		}
		@Override
		public void to(Object value) {
			map.put(key, value);
		}
	}
	
}
