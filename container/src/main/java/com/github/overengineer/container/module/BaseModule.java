package com.github.overengineer.container.module;

import com.github.overengineer.container.key.ClassKey;
import com.github.overengineer.container.key.GenericKey;
import com.github.overengineer.container.key.SerializableKey;

import java.util.*;

/**
 * @author rees.byars
 */
public abstract class BaseModule implements Module {

    private final List<Mapping<?>> mappings = new LinkedList<Mapping<?>>();
    private final Set<SerializableKey> managedComponentFactories = new LinkedHashSet<SerializableKey>();
    private final Map<SerializableKey, Class> nonManagedComponentFactories = new HashMap<SerializableKey, Class>();

    public BaseModule() {
        configure();
    }

    @Override
    public final List<Mapping<?>> getMappings() {
        return mappings;
    }

    @Override
    public Set<SerializableKey> getManagedComponentFactories() {
        return managedComponentFactories;
    }

    @Override
    public Map<SerializableKey, Class> getNonManagedComponentFactories() {
        return nonManagedComponentFactories;
    }

    protected abstract void configure();

    protected  <V> MutableMapping<V> use(Class<V> implementationClass) {
        TypeMapping<V> mapping = new TypeMapping<V>(implementationClass);
        mappings.add(mapping);
        return mapping;
    }

    protected  <V> MutableMapping<V> use(GenericKey<V> implementationGeneric) {
        TypeMapping<V> mapping = new GenericMapping<V>(implementationGeneric);
        mappings.add(mapping);
        return mapping;
    }

    protected  <V> MutableMapping<V> use(V implementation) {
        InstanceMappingImpl<V> mapping = new InstanceMappingImpl<V>(implementation);
        mappings.add(mapping);
        return mapping;
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
