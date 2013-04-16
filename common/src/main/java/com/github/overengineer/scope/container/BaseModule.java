package com.github.overengineer.scope.container;

import java.util.*;

public class BaseModule implements Module {

    private Map<Class<?>, List<Class<?>>> typeMappings = new LinkedHashMap<Class<?>, List<Class<?>>>();
    private Map<Class<?>, Object> instanceMappings = new LinkedHashMap<Class<?>, Object>();
    private Map<String, Object> properties = new LinkedHashMap<String, Object>();

    @Override
    public final Map<Class<?>, List<Class<?>>> getTypeMappings() {
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
        private Map<Class<?>, List<Class<?>>> map;
        public TypeMapper(Class<V> value, Map<Class<?>, List<Class<?>>> map) {
            this.value = value;
            this.map = map;
        }
        public TypeMapper<V> forType(Class<? super V> key) {
            List<Class<?>> mappings = map.get(key);
            if (mappings == null) {
                mappings = new LinkedList<Class<?>>();
                map.put(key, mappings);
            }
            mappings.add(value);
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
