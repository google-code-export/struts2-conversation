package com.github.overengineer.container;

import com.github.overengineer.container.key.ClassKey;
import com.github.overengineer.container.key.GenericKey;
import com.github.overengineer.container.key.SerializableKey;

import java.util.*;

/**
 * @author rees.byars
 */
public abstract class BaseModule implements Module {

    private final Map<Class<?>, List<Class<?>>> typeMappings = new LinkedHashMap<Class<?>, List<Class<?>>>();
    private final Map<Class<?>, Object> instanceMappings = new LinkedHashMap<Class<?>, Object>();
    private final Map<SerializableKey, List<Class<?>>> genericTypeMappings = new LinkedHashMap<SerializableKey, List<Class<?>>>();
    private final Map<SerializableKey, Object> genericInstanceMappings = new LinkedHashMap<SerializableKey, Object>();
    private final Set<SerializableKey> managedComponentFactories = new LinkedHashSet<SerializableKey>();
    private final Map<SerializableKey, Class> nonManagedComponentFactories = new HashMap<SerializableKey, Class>();
    private final Map<String, Object> properties = new LinkedHashMap<String, Object>();

    public BaseModule() {
        configure();
    }

    @Override
    public final Map<Class<?>, List<Class<?>>> getTypeMappings() {
        return typeMappings;
    }

    @Override
    public Map<Class<?>, Object> getInstanceMappings() {
        return instanceMappings;
    }

    @Override
    public Map<SerializableKey, List<Class<?>>> getGenericTypeMappings() {
        return genericTypeMappings;
    }

    @Override
    public Map<SerializableKey, Object> getGenericInstanceMappings() {
        return genericInstanceMappings;
    }

    @Override
    public Set<SerializableKey> getManagedComponentFactories() {
        return managedComponentFactories;
    }

    @Override
    public Map<SerializableKey, Class> getNonManagedComponentFactories() {
        return nonManagedComponentFactories;
    }

    @Override
    public final Map<String, Object> getProperties() {
        return properties;
    }

    protected abstract void configure();

    protected  <V> TypeMapper<V> use(Class<V> implementationClass) {
        return new TypeMapper<V>(implementationClass);
    }

    protected  <V> InstanceMapper<V> useInstance(V instance) {
        return new InstanceMapper<V>(instance);
    }

    protected PropertyMapper set(String name) {
        return new PropertyMapper(name);
    }

    protected void registerManagedComponentFactory(GenericKey factoryKey) {
        managedComponentFactories.add(factoryKey);
    }

    protected NonManagedComponentFactoryMapper registerNonManagedComponentFactory(Class factoryType) {
        return new NonManagedComponentFactoryMapper(factoryType);
    }

    protected NonManagedComponentFactoryMapper registerNonManagedComponentFactory(GenericKey factoryKey) {
        return new NonManagedComponentFactoryMapper(factoryKey);
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
        public TypeMapper<V> forGeneric(GenericKey key) {
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
        public InstanceMapper<V> forGeneric(GenericKey key) {
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

    public class NonManagedComponentFactoryMapper {
        private final SerializableKey key;
        public NonManagedComponentFactoryMapper(Class key) {
            this.key = new ClassKey(key);
        }
        public NonManagedComponentFactoryMapper(SerializableKey key) {
            this.key = key;
        }
        public void toProduce(Class value) {
            nonManagedComponentFactories.put(key, value);
        }
    }

}
