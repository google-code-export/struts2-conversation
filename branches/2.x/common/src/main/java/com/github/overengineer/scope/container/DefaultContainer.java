package com.github.overengineer.scope.container;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * TODO nested, scoped containers and scoped proxies
 */
public class DefaultContainer implements Container {

    private static final Logger LOG = LoggerFactory.getLogger(DefaultContainer.class);

    protected Map<Class<?>, Class<?>> mappings = new HashMap<Class<?>, Class<?>>();
    protected Map<Class<?>, ComponentStrategy<?>> strategies = new HashMap<Class<?>, ComponentStrategy<?>>();
    protected Map<String, Object> properties = new HashMap<String, Object>();
    protected List<ComponentInitializationListener> initializationListeners = new ArrayList<ComponentInitializationListener>();
    protected ComponentStrategyFactory strategyFactory;

    public DefaultContainer(ComponentStrategyFactory strategyFactory) {
        this.strategyFactory = strategyFactory;
        addInstance(Provider.class, this);
        addInstance(ComponentProvider.class, this);
        addInstance(PropertyProvider.class, this);
        addInstance(Container.class, this);
    }

    @Override
    public void verify() throws WiringException {
        LOG.info("Verifying container.");
        try {
            for (Class<?> componentType : mappings.keySet()) {
                get(componentType);
            }
        } catch (Exception e) {
            throw new WiringException("An exception occurred while verifying the container", e);
        }
        LOG.info("Container verified.");
    }

    @Override
    public Container loadModule(Module module) {
        for (Map.Entry<Class<?>, Class<?>> componentEntry : module.getTypeMappings().entrySet()) {
            addMapping(componentEntry.getKey(), componentEntry.getValue());
        }
        for (Map.Entry<Class<?>, Object> componentEntry : module.getInstanceMappings().entrySet()) {
            addMapping(componentEntry.getKey(), componentEntry.getValue());
        }
        for (Map.Entry<String, Object> propertyEntry : module.getProperties().entrySet()) {
            addProperty(propertyEntry.getKey(), propertyEntry.getValue());
        }
        return this;
    }

    @Override
    public <T> Container add(Class<T> componentType, Class<? extends T> implementationType) {
        addMapping(componentType, implementationType);
        return this;
    }

    @Override
    public <T, I extends T> Container addInstance(Class<T> componentType, I implementation) {
        addMapping(componentType, implementation);
        return this;
    }

    @Override
    public Container addProperty(String propertyName, Object propertyValue) {
        properties.put(propertyName, propertyValue);
        return this;
    }

    @Override
    public Container addListener(ComponentInitializationListener listener) {
        initializationListeners.add(listener);
        return this;
    }

    @Override
    public <T> T get(Class<T> clazz) {
        Class<?> implementationType = mappings.get(clazz);
        if (implementationType == null) {
            throw new MissingDependencyException("No components of type [" + clazz.getName() + "] have been registered with the container");
        }
        @SuppressWarnings("unchecked")
        ComponentStrategy<T> strategy = (ComponentStrategy<T>) strategies.get(implementationType);
        return strategy.get(this, initializationListeners);
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> T getProperty(Class<T> clazz, String name) {
        Object property = properties.get(name);
        if (property == null) {
            throw new MissingDependencyException("No property of name [" + name + "] has been registered with the container");
        }
        return (T) property;
    }

    protected void addMapping(Class<?> type, Class<?> implementationType) {

        if (!type.isInterface()) {
            throw new BadDesignException("We force you to map dependencies only to interfaces. The type [" + type.getName() + "] is not an interface.  Don't like it?  I don't give a shit.");
        }

        strategies.put(implementationType, strategyFactory.create(implementationType));
        mappings.put(type, implementationType);
    }

    protected void addMapping(Class<?> type, Object implementation) {

        if (!type.isInterface()) {
            throw new BadDesignException("We force you to map dependencies only to interfaces. The type [" + type.getName() + "] is not an interface.  Don't like it?  I don't give a shit.");
        }

        strategies.put(implementation.getClass(), strategyFactory.create(implementation));
        mappings.put(type, implementation.getClass());
    }

}
