package com.github.overengineer.scope.container;

import java.util.HashMap;
import java.util.Map;

public class BaseModule implements Module {

    private Map<Class<?>, Class<?>> typeMappings = new HashMap<Class<?>, Class<?>>();
    private Map<Class<?>, Object> instanceMappings = new HashMap<Class<?>, Object>();
    private Map<String, Object> properties = new HashMap<String, Object>();

    @Override
    public final Map<Class<?>, Class<?>> getTypeMappings() {
        return typeMappings;
    }

    @Override
    public Map<Class<?>, Object> getInstanceMappings() {
        return instanceMappings;
    }

    @Override
    public final Map<String, Object> getProperties() {
        return properties;
    }

    public <V> TypeMapper<V> use(Class<V> implementationClass) {
        return new TypeMapper<V>(implementationClass, typeMappings);
    }

    public <V> InstanceMapper<V> useInstance(V instance) {
        return new InstanceMapper<V>(instance, instanceMappings);
    }

    public PropertyMapper set(String name) {
        return new PropertyMapper(name, properties);
    }

    public static class TypeMapper<V> {
        private Class<V> value;
        private Map<Class<?>, Class<?>> map;
        public TypeMapper(Class<V> value, Map<Class<?>, Class<?>> map) {
            this.value = value;
            this.map = map;
        }
        public TypeMapper<V> forType(Class<? super V> key) {
            map.put(key, value);
            return this;
        }

    }

    public static class InstanceMapper<V> {
        private V value;
        private Map<Class<?>, Object> map;
        public InstanceMapper(V value, Map<Class<?>, Object> map) {
            this.value = value;
            this.map = map;
        }
        public InstanceMapper<V> forType(Class<? super V> key) {
            map.put(key, value);
            return this;
        }
    }

    public static class PropertyMapper {
        private String key;
        private Map<String, Object> map;
        public PropertyMapper(String key, Map<String, Object> map) {
            this.key = key;
            this.map = map;
        }
        public void to(Object value) {
            map.put(key, value);
        }
    }

}
