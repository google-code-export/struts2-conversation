package com.github.overengineer.scope.container.standalone;
import com.github.overengineer.scope.container.BaseScopeContainer;
import com.github.overengineer.scope.container.ScopeContainer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

public class SimpleScopeContainer extends BaseScopeContainer implements StandaloneContainer {

    private static final long serialVersionUID = -3502345525425524764L;

    private static final Logger LOG = LoggerFactory.getLogger(SimpleScopeContainer.class);

    private Map<Class<?>, Instantiator<?>> instantiators = new HashMap<Class<?>, Instantiator<?>>();
    private Map<String, Object> properties = new HashMap<String, Object>();

    public SimpleScopeContainer() {
        addInstance(ScopeContainer.class, this);
        addInstance(StandaloneContainer.class, this);
    }

    @Override
    public void verify() throws WiringException {
        LOG.info("Verifying container.");
        try {
            for (Class<?> componentType : instantiators.keySet()) {
                getComponent(componentType);
            }
        } catch (Exception e) {
            throw new WiringException("An exception occurred while verifying the container", e);
        }
        LOG.info("Container verified.");
    }

    @Override
    public StandaloneContainer loadModule(Module module) {
        for (Entry<Class<?>, Class<?>> componentEntry : module.getTypeMappings().entrySet()) {
            try {
                instantiators.put(componentEntry.getKey(), Instantiator.Factory.create(componentEntry.getValue()));
            } catch (Exception e) {
                throw new RuntimeException("There was an error attempting to create component of type [" + componentEntry.getValue().getName() + "]", e);
            }
        }
        for (Entry<Class<?>, Object> componentEntry : module.getInstanceMappings().entrySet()) {
            instantiators.put(componentEntry.getKey(), Instantiator.Factory.wrap(componentEntry.getValue()));
        }
        for (Entry<String, Object> propertyEntry : module.getProperties().entrySet()) {
            addProperty(propertyEntry.getKey(), propertyEntry.getValue());
        }
        return this;
    }

    @Override
    public <T> StandaloneContainer add(Class<T> componentType, Class<? extends T> implementationType) {
        try {
            instantiators.put(componentType, Instantiator.Factory.create(implementationType));
        } catch (Exception e) {
            throw new RuntimeException("There was an error attempting to create a component", e);
        }
        return this;
    }

    @Override
    public <T, I extends T> StandaloneContainer addInstance(Class<T> componentType, I implementation) {
        instantiators.put(componentType, Instantiator.Factory.wrap(implementation));
        return this;
    }

    @Override
    public StandaloneContainer addProperty(String propertyName, Object propertyValue) {
        properties.put(propertyName, propertyValue);
        return this;
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> T getProperty(Class<T> clazz, String name) {
        Object property = properties.get(name);
        if (property == null) {
            throw new RuntimeException("No property of name [" + name + "] has been registered with the container");
        }
        return (T) property;
    }

    @SuppressWarnings("unchecked")
    @Override
    protected <T> T getSingletonComponent(Class<T> clazz) {
        return (T) instantiators.get(clazz).getInstance(this);
    }

    @SuppressWarnings("unchecked")
    @Override
    protected <T> T getNewComponentInstance(Class<T> clazz) {
        return (T) instantiators.get(clazz).getInstance(this);
    }

    @SuppressWarnings("unchecked")
    @Override
    protected <T> Class<? extends T> getImplementationType(Class<T> clazz) {
        Instantiator<T> instantiator = (Instantiator<T>) instantiators.get(clazz);
        if (instantiator == null) {
            throw new RuntimeException("No components of type [" + clazz.getName() + "] have been registered with the container");
        }
        return instantiator.getTargetType();
    }

}
