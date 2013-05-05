package com.github.overengineer.scope.container;

import java.util.*;

/**
 * @author rees.byars
 */
public class BaseModule implements Module {

    private final Map<Class<?>, List<Class<?>>> typeMappings = new LinkedHashMap<Class<?>, List<Class<?>>>();
    private final Map<Class<?>, Object> instanceMappings = new LinkedHashMap<Class<?>, Object>();
    private final Map<Key, List<Class<?>>> genericTypeMappings = new LinkedHashMap<Key, List<Class<?>>>();
    private final Map<Key, Object> genericInstanceMappings = new LinkedHashMap<Key, Object>();
    private final Map<String, Object> properties = new LinkedHashMap<String, Object>();

    @Override
    public final Map<Class<?>, List<Class<?>>> getTypeMappings() {
        return typeMappings;
    }

    @Override
    public Map<Class<?>, Object> getInstanceMappings() {
        return instanceMappings;
    }

    @Override
    public Map<Key, List<Class<?>>> getGenericTypeMappings() {
        return genericTypeMappings;
    }

    @Override
    public Map<Key, Object> getGenericInstanceMappings() {
        return genericInstanceMappings;
    }

    @Override
    public final Map<String, Object> getProperties() {
        return properties;
    }

    public <V> TypeMapper<V> use(Class<V> implementationClass) {
        return new TypeMapper<V>(implementationClass);
    }

    public <V> InstanceMapper<V> useInstance(V instance) {
        return new InstanceMapper<V>(instance);
    }

    public PropertyMapper set(String name) {
        return new PropertyMapper(name);
    }

    public class TypeMapper<V> {
        private final Class<V> value;
        public TypeMapper(Class<V> value) {
            this.value = value;
        }
        public TypeMapper<V> forType(Class<? super V> key) {
            List<Class<?>> mappings = typeMappings.get(key);
            if (mappings == null) {
                mappings = new LinkedList<Class<?>>();
                typeMappings.put(key, mappings);
            }
            mappings.add(value);
            return this;
        }
        public TypeMapper<V> forGeneric(Key.Generic key) {
            List<Class<?>> mappings = genericTypeMappings.get(key);
            if (mappings == null) {
                mappings = new LinkedList<Class<?>>();
                genericTypeMappings.put(key, mappings);
            }
            mappings.add(value);
            return this;
        }
    }

    public class InstanceMapper<V> {
        private final V value;
        public InstanceMapper(V value) {
            this.value = value;
        }
        public InstanceMapper<V> forType(Class<? super V> key) {
            instanceMappings.put(key, value);
            return this;
        }
        public InstanceMapper<V> forGeneric(Key.Generic key) {
            genericInstanceMappings.put(key, value);
            return this;
        }
    }

    public class PropertyMapper {
        private final String key;
        public PropertyMapper(String key) {
            this.key = key;
        }
        public void to(Object value) {
            properties.put(key, value);
        }
    }

}
