package com.github.overengineer.scope.container;
import com.github.overengineer.scope.CommonModule;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

public class SimpleScopeContainer extends AbstractScopeContainer implements StandaloneContainer {

    private static final long serialVersionUID = -3502345525425524764L;

    private Map<Class<?>, Object> components = new HashMap<Class<?>, Object>();
    private Map<String, Object> properties = new HashMap<String, Object>();

    public SimpleScopeContainer() {
        addInstance(ScopeContainer.class, this);
        addInstance(StandaloneContainer.class, this);
        loadModule(new CommonModule());
    }

    @Override
    public StandaloneContainer loadModule(Module module) {
        for (Entry<Class<?>, Class<?>> componentEntry : module.getTypeMappings().entrySet()) {
            try {
                components.put(componentEntry.getKey(), componentEntry.getValue().newInstance());
            } catch (Exception e) {
                throw new RuntimeException("There was an error attempting to create a component", e);
            }
        }
        for (Entry<Class<?>, Object> componentEntry : module.getInstanceMappings().entrySet()) {
            components.put(componentEntry.getKey(), componentEntry.getValue());
        }
        for (Entry<String, Object> propertyEntry : module.getProperties().entrySet()) {
            addProperty(propertyEntry.getKey(), propertyEntry.getValue());
        }
        return this;
    }

    @Override
    public <T> StandaloneContainer add(Class<T> componentType, Class<? extends T> implementationType) {
        try {
            components.put(componentType, implementationType.newInstance());
        } catch (Exception e) {
            throw new RuntimeException("There was an error attempting to create a component", e);
        }
        return this;
    }

    @Override
    public <T, I extends T> StandaloneContainer addInstance(Class<T> componentType, I implementation) {
        components.put(componentType, implementation);
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
        return (T) properties.get(name);
    }

    @SuppressWarnings("unchecked")
    @Override
    protected <T> T getComponentFromPrimaryContainer(Class<T> clazz) {
        return (T) components.get(clazz);
    }

    @SuppressWarnings("unchecked")
    @Override
    protected <T> Class<? extends T> getImplementationType(Class<T> clazz) {
        return (Class<? extends T>) components.get(clazz).getClass();
    }

}
