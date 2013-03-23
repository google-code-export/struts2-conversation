package com.github.overengineer.scope.container;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class AbstractScopeContainer implements ScopeContainer {

    private static final long serialVersionUID = -6820777796732236492L;
    private static final Logger LOG = LoggerFactory.getLogger(AbstractScopeContainer.class);

    private final Map<Class<?>, InjectionStrategy<?>> strategies = new HashMap<Class<?>, InjectionStrategy<?>>();

    @Override
    public <T> T getComponent(Class<T> clazz) {
        @SuppressWarnings("unchecked")
        InjectionStrategy<T> strategy = (InjectionStrategy<T>) strategies.get(clazz);
        if (strategy == null) {
            strategy = InjectionStrategy.Factory.create(getInjectionContext(clazz));
            synchronized (strategies) {
                LOG.debug("Adding InjectionStrategy of type [{}] for component of type [{}]", strategy.getClass().getName(), clazz.getName());
                if (strategies.get(clazz) == null) {
                    strategies.put(clazz, strategy);
                }
            }
        }
        return strategy.getComponent();
    }

    protected <T> InjectionContextImpl<T> getInjectionContext(Class<T> componentClass) {
        return new InjectionContextImpl<T>(componentClass);
    }

    class InjectionContextImpl<T> implements InjectionContext<T> {

        Class<? extends T> implementationType;
        T singletonComponent;

        InjectionContextImpl(Class<T> componentType) {
            this.implementationType = AbstractScopeContainer.this.getImplementationType(componentType);
            this.singletonComponent = AbstractScopeContainer.this.getComponentFromPrimaryContainer(componentType);
        }

        @Override
        public T getSingletonComponent() {
            return singletonComponent;
        }

        @Override
        public T getPrototypeComponent() {
            try {
                return implementationType.newInstance();
            } catch (Exception e) {
                throw new RuntimeException("Could not create new instance of component", e);
            }
        }

        @Override
        public Class<? extends T> getImplementationType() {
            return implementationType;
        }

        @Override
        public ScopeContainer getContainer() {
            return AbstractScopeContainer.this;
        }

    }

    protected abstract <T> T getComponentFromPrimaryContainer(Class<T> clazz);

    protected abstract <T> Class<? extends T> getImplementationType(Class<T> clazz);

}
