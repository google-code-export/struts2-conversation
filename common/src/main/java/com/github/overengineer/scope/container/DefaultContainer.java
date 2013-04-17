package com.github.overengineer.scope.container;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

/**
 *
 * TODO Features:
 * TODO nested, scoped containers and scoped proxies, factories, complete interceptor rules (@OR, @AND, @NOT)
 * TODO consider a sort of decorator placeholder strategy to handle decorator being added before delegate that throws decoration exception if invoked
 * TODO then combine this with a check on existing strategies for this type and handling it appropriately
 * TODO DecoratableContainer + LifecycleContainer
 *
 * TODO Tech debt:
 * TODO cleanup interceptor impl, move from extensions to decorations
 * TODO throw decorationexception if a defaultinstantiator tries to reference itself
 * TODO make everything serializable as much as possible
 */
public class DefaultContainer implements Container {

    private static final Logger LOG = LoggerFactory.getLogger(DefaultContainer.class);

    protected final Map<Class<?>, Class<?>> mappings = new HashMap<Class<?>, Class<?>>();
    protected final Map<Class<?>, ComponentStrategy<?>> strategies = new HashMap<Class<?>, ComponentStrategy<?>>();
    protected final Map<String, Object> properties = new HashMap<String, Object>();
    protected final List<ComponentInitializationListener> initializationListeners = new ArrayList<ComponentInitializationListener>();
    protected final ComponentStrategyFactory strategyFactory;

    public DefaultContainer(ComponentStrategyFactory strategyFactory) {
        this.strategyFactory = strategyFactory;
        addInstance(Container.class, this);
        addInstance(Provider.class, this);
        addInstance(ComponentProvider.class, this);
        addInstance(PropertyProvider.class, this);
    }

    @Override
    public Container start() {
        addProperty(Properties.LISTENERS, initializationListeners);
        return get(Container.class);
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
        for (Map.Entry<Class<?>, List<Class<?>>> componentEntry : module.getTypeMappings().entrySet()) {
            for (Class<?> cls : componentEntry.getValue()) {
                addMapping(componentEntry.getKey(), cls);
            }
        }
        for (Map.Entry<Class<?>, Object> componentEntry : module.getInstanceMappings().entrySet()) {
            addMapping(componentEntry.getKey(), componentEntry.getValue());
        }
        for (Map.Entry<String, Object> propertyEntry : module.getProperties().entrySet()) {
            addProperty(propertyEntry.getKey(), propertyEntry.getValue());
        }
        return get(Container.class);
    }

    @Override
    public Container addListener(Class<? extends ComponentInitializationListener> listenerClass) {
        ComponentStrategy strategy = strategyFactory.create(listenerClass, Collections.<ComponentInitializationListener>emptyList());
        initializationListeners.add((ComponentInitializationListener) strategy.get(this));
        return get(Container.class);
    }

    @Override
    public <T> Container add(Class<T> componentType, Class<? extends T> implementationType) {
        addMapping(componentType, implementationType);
        return get(Container.class);
    }

    @Override
    public <T, I extends T> Container addInstance(Class<T> componentType, I implementation) {
        addMapping(componentType, implementation);
        return get(Container.class);
    }

    @Override
    public Container addProperty(String propertyName, Object propertyValue) {
        properties.put(propertyName, propertyValue);
        return get(Container.class);
    }

    @Override
    public <T> T get(Class<T> clazz) {
        Class<?> implementationType = mappings.get(clazz);
        if (implementationType == null) {
            throw new MissingDependencyException("No components of type [" + clazz.getName() + "] have been registered with the container");
        }
        @SuppressWarnings("unchecked")
        ComponentStrategy<T> strategy = (ComponentStrategy<T>) strategies.get(implementationType);
        return strategy.get(this);
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

        Class<?> existing = mappings.get(type);
        if (existing != null) {
            strategies.put(implementationType, strategyFactory.createDecoratorStrategy(implementationType, initializationListeners, existing, strategies.get(existing)));
        } else {
            strategies.put(implementationType, strategyFactory.create(implementationType, initializationListeners));
        }

        mappings.put(type, implementationType);
    }

    protected void addMapping(Class<?> type, Object implementation) {

        if (!type.isInterface()) {
            throw new BadDesignException("We force you to map dependencies only to interfaces. The type [" + type.getName() + "] is not an interface.  Don't like it?  I don't give a shit.");
        }

        strategies.put(implementation.getClass(), strategyFactory.createInstanceStrategy(implementation, initializationListeners));
        mappings.put(type, implementation.getClass());
    }

}
