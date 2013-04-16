package com.github.overengineer.scope.container;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 *
 * TODO Features:
 * TODO nested, scoped containers and scoped proxies, factories, complete interceptor rules
 *
 * TODO Tech debt:
 * TODO cleanup interceptor impl, move from extensions to decorations, add module loading to the bootstrapper
 * TODO get rid of bootstrapper, re-add addListener, remove from constructor - default container can builder other containers
 * TODO throw decorationexception if a defaultinstantiator tries to reference itself
 * TODO consider a sort of decorator placeholder strategy to handle decorator being added before delegate that throws decoration exception if invoked
 * TODO then combine this with a check on existing strategies for this type and handling it appropriately
 * TODO returning "this" from container gives non-proxy version.
 */
public class DefaultContainer extends BootstrapContainer implements Container {

    private static final Logger LOG = LoggerFactory.getLogger(DefaultContainer.class);

    private List<Class<? extends ComponentInitializationListener>> initializationListenerClasses;

    public DefaultContainer(ComponentStrategyFactory strategyFactory, @Property(Properties.LISTENERS) List<Class<? extends ComponentInitializationListener>> initializationListenerClasses) {
        this.strategyFactory = strategyFactory;
        this.initializationListenerClasses = initializationListenerClasses;
        addInstance(Provider.class, this);
        addInstance(ComponentProvider.class, this);
        addInstance(PropertyProvider.class, this);
        addInstance(Container.class, this);
    }

    @Override
    public Container start() {
        List<ComponentInitializationListener> tempList = new LinkedList<ComponentInitializationListener>();
        for (Class<? extends ComponentInitializationListener> listenerClass : initializationListenerClasses) {
            ComponentStrategy strategy = strategyFactory.create(listenerClass, initializationListeners);
            tempList.add((ComponentInitializationListener) strategy.get(this));
        }
        initializationListeners.addAll(tempList);
        return this;
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

}
