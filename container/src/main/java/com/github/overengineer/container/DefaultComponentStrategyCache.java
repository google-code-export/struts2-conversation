package com.github.overengineer.container;

import com.github.overengineer.container.key.KeyRepository;
import com.github.overengineer.container.key.SerializableKey;
import com.github.overengineer.container.metadata.Prototype;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

/**
 * @author rees.byars
 */
@Prototype
public class DefaultComponentStrategyCache implements ComponentStrategyCache {

    private final Map<SerializableKey, Class<?>> mappings = new HashMap<SerializableKey, Class<?>>();
    private final Map<Class<?>, ComponentStrategy<?>> strategies = new HashMap<Class<?>, ComponentStrategy<?>>();
    private final ComponentStrategyFactory strategyFactory;
    private final KeyRepository keyRepository;

    public DefaultComponentStrategyCache(ComponentStrategyFactory strategyFactory, KeyRepository keyRepository) {
        this.strategyFactory = strategyFactory;
        this.keyRepository = keyRepository;
    }

    @Override
    public <T> void add(Class<T> componentType, Class<? extends T> implementationType) {
        add(keyRepository.retrieveKey(componentType), implementationType);
    }

    @Override
    public void add(SerializableKey key, Class implementationType) {
        addMapping(key, implementationType);
    }

    @Override
    public <T, I extends T> void addInstance(Class<T> componentType, I implementation) {
        addInstance(keyRepository.retrieveKey(componentType), implementation);
    }

    @Override
    public void addInstance(SerializableKey key, Object implementation) {
        addMapping(key, implementation);
    }

    @Override
    public <T> ComponentStrategy<T> get(Class<T> clazz) {
        return null;
    }

    @Override
    public <T> ComponentStrategy<T> get(Type type) {
        return null;
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> ComponentStrategy<T> get(SerializableKey key) {
        Class<?> implementationType = mappings.get(key);
        if (implementationType == null) {
            return null;
        }
        return (ComponentStrategy<T>) strategies.get(implementationType);
    }

    protected void addMapping(SerializableKey key, Class<?> implementationType) {

        if (!key.getTargetClass().isInterface()) {
            throw new BadDesignException("We force you to map dependencies only to interfaces. The type [" + key.getTargetClass().getName() + "] is not an interface.  Don't like it?  I don't give a shit.");
        }

        Class<?> existing = mappings.get(key);
        if (existing != null) {
            strategies.put(implementationType, strategyFactory.createDecoratorStrategy(implementationType, existing, strategies.get(existing)));
        } else {
            keyRepository.addKey(key);
            strategies.put(implementationType, strategyFactory.create(implementationType));
        }

        mappings.put(key, implementationType);
    }

    protected void addMapping(SerializableKey key, Object implementation) {

        if (!key.getTargetClass().isInterface()) {
            throw new BadDesignException("We force you to map dependencies only to interfaces. The type [" + key.getTargetClass().getName() + "] is not an interface.  Don't like it?  I don't give a shit.");
        }

        keyRepository.addKey(key);
        strategies.put(implementation.getClass(), strategyFactory.createInstanceStrategy(implementation));
        mappings.put(key, implementation.getClass());
    }
}
