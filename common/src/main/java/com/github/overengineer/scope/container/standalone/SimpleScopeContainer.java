package com.github.overengineer.scope.container.standalone;
import com.github.overengineer.scope.CommonModule;
import com.github.overengineer.scope.container.BaseScopeContainer;
import com.github.overengineer.scope.container.ScopeContainer;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

public class SimpleScopeContainer extends BaseScopeContainer implements StandaloneContainer {

    private static final long serialVersionUID = -3502345525425524764L;

    private Map<Class<?>, Instantiator<?>> instantiators = new HashMap<Class<?>, Instantiator<?>>();
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
        return (T) properties.get(name);
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
        return (Class<? extends T>) instantiators.get(clazz).getTargetType();
    }

}
