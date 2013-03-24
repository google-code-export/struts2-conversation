package com.github.overengineer.scope.container;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class BaseScopeContainer implements ScopeContainer {

    private static final long serialVersionUID = -6820777796732236492L;
    private static final Logger LOG = LoggerFactory.getLogger(BaseScopeContainer.class);

    private final Map<Class<?>, InjectionStrategy<?>> strategies = new HashMap<Class<?>, InjectionStrategy<?>>();

    @SuppressWarnings("unchecked")
    @Override
    public <T> T getComponent(Class<T> clazz) {
        InjectionStrategy<T> strategy = (InjectionStrategy<T>) strategies.get(clazz);
        if (strategy == null) {
            synchronized (strategies) {
                strategy = (InjectionStrategy<T>) strategies.get(clazz);
                if (strategies.get(clazz) == null) {
                    strategy = InjectionStrategy.Factory.create(getInjectionContext(clazz));
                    LOG.debug("Adding InjectionStrategy of type [{}] for component of type [{}]", strategy.getClass().getSimpleName(), clazz.getName());
                    strategies.put(clazz, strategy);
                }
            }
        }
        return strategy.getComponent();
    }

    protected <T> InjectionContext<T> getInjectionContext(Class<T> componentClass) {
        return new InjectionContextImpl<T>(componentClass);
    }

    class InjectionContextImpl<T> implements InjectionContext<T> {

        Class<T> componentType;
        Class<? extends T> implementationType;

        InjectionContextImpl(Class<T> componentType) {
            this.componentType = componentType;
            implementationType = BaseScopeContainer.this.getImplementationType(componentType);
        }

        @Override
        public T getSingletonComponent() {
            return BaseScopeContainer.this.getSingletonComponent(componentType);
        }

        @Override
        public T getPrototypeComponent() {
            return BaseScopeContainer.this.getNewComponentInstance(componentType);
        }

        @Override
        public Class<? extends T> getImplementationType() {
            return implementationType;
        }

        @Override
        public ScopeContainer getContainer() {
            return BaseScopeContainer.this;
        }

    }

    protected abstract <T> T getSingletonComponent(Class<T> clazz);

    protected abstract <T> T getNewComponentInstance(Class<T> clazz);

    protected abstract <T> Class<? extends T> getImplementationType(Class<T> clazz);

}
