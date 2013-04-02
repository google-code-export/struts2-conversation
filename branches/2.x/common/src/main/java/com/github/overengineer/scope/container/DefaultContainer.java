package com.github.overengineer.scope.container;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * TODO handle cyclic refs
 *
 * TODO nested containers
 *
 * TODO singleton instance repository so that one instance can be used across multiple interface mappings -
 * this can also be used to support hotswapping of proxies
 *
 * TODO Container container = Orb.Builder.with(postProcessors).with(proxyStrategyFactory).build();
 */
public class DefaultContainer implements Container {

    private static final Logger LOG = LoggerFactory.getLogger(DefaultContainer.class);

    private Map<Class<?>, ComponentStrategy<?>> strategies = new HashMap<Class<?>, ComponentStrategy<?>>();
    private Map<String, Object> properties = new HashMap<String, Object>();
    private List<ComponentInitializationListener> initializationListeners = new ArrayList<ComponentInitializationListener>();
    private ComponentStrategyFactory strategyFactory;

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
            for (Class<?> componentType : strategies.keySet()) {
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
            try {
                strategies.put(componentEntry.getKey(), strategyFactory.create(componentEntry.getValue()));
            } catch (Exception e) {
                throw new RuntimeException("There was an error attempting to create component of type [" + componentEntry.getValue().getName() + "]", e);
            }
        }
        for (Map.Entry<Class<?>, Object> componentEntry : module.getInstanceMappings().entrySet()) {
            strategies.put(componentEntry.getKey(), strategyFactory.create(componentEntry.getValue()));
        }
        for (Map.Entry<String, Object> propertyEntry : module.getProperties().entrySet()) {
            addProperty(propertyEntry.getKey(), propertyEntry.getValue());
        }
        return this;
    }

    @Override
    public <T> Container add(Class<T> componentType, Class<? extends T> implementationType) {
        try {
            strategies.put(componentType, strategyFactory.create(implementationType));
        } catch (Exception e) {
            throw new RuntimeException("There was an error attempting to create a component", e);
        }
        return this;
    }

    @Override
    public <T, I extends T> Container addInstance(Class<T> componentType, I implementation) {
        strategies.put(componentType, strategyFactory.create(implementation));
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
        @SuppressWarnings("unchecked")
        ComponentStrategy<T> strategy = (ComponentStrategy<T>) strategies.get(clazz);
        if (strategy == null) {
            throw new MissingDependencyException("No components of type [" + clazz.getName() + "] have been registered with the container");
        }
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

}
