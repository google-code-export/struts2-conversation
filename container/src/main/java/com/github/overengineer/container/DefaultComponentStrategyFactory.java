package com.github.overengineer.container;

import com.github.overengineer.container.inject.ComponentInjector;
import com.github.overengineer.container.inject.InjectorFactory;
import com.github.overengineer.container.inject.MethodInjector;
import com.github.overengineer.container.instantiate.*;
import com.github.overengineer.container.metadata.MetadataAdapter;
import com.github.overengineer.container.scope.Scope;
import com.github.overengineer.container.scope.Scopes;

import java.lang.reflect.Method;
import java.util.List;

/**
 * @author rees.byars
 */
public class DefaultComponentStrategyFactory implements ComponentStrategyFactory {

    private final MetadataAdapter metadataAdapter;
    private final InjectorFactory injectorFactory;
    private final InstantiatorFactory instantiatorFactory;
    private final List<ComponentInitializationListener> initializationListeners;

    public DefaultComponentStrategyFactory(MetadataAdapter metadataAdapter, InjectorFactory injectorFactory, InstantiatorFactory instantiatorFactory, List<ComponentInitializationListener> initializationListeners) {
        this.metadataAdapter = metadataAdapter;
        this.injectorFactory = injectorFactory;
        this.instantiatorFactory = instantiatorFactory;
        this.initializationListeners = initializationListeners;
    }

    @Override
    public <T> ComponentStrategy<T> create(Class<T> implementationType, Scope scope) {
        ComponentInjector<T> injector = injectorFactory.create(implementationType);
        Instantiator<T> instantiator = instantiatorFactory.create(implementationType);
        Scope theScope = metadataAdapter.getScope(implementationType);
        if (theScope != null) {
            if (!Scopes.SINGLETON.equals(scope)) {
                throw new IllegalStateException("The class [" + implementationType.getName() + "] is annotated with a scope but also has a scope specified in a module.  One approach must be chosen.");
            }
        } else {
            theScope = scope;
        }
        if (Scopes.PROTOTYPE.equals(theScope)) {
            return new PrototypeComponentStrategy<T>(injector, instantiator, initializationListeners);
        } else {
            return new SingletonComponentStrategy<T>(new PrototypeComponentStrategy<T>(injector, instantiator, initializationListeners));
        }
    }

    @Override
    public <T> ComponentStrategy<T> createInstanceStrategy(T implementation) {
        @SuppressWarnings("unchecked")
        Class<T> clazz = (Class<T>) implementation.getClass();
        ComponentInjector<T> injector = injectorFactory.create(clazz);
        return new InstanceStrategy<T>(implementation, injector, initializationListeners);
    }

    @Override
    public <T> ComponentStrategy<T> createCustomStrategy(ComponentStrategy providerStrategy) {
        Method providerMethod = metadataAdapter.getCustomProviderMethod(providerStrategy.getComponentType());
        @SuppressWarnings("unchecked")
        MethodInjector<T> methodInjector = injectorFactory.create(providerStrategy.getComponentType(), providerMethod);
        return new CustomComponentStrategy<T>(providerStrategy, methodInjector, providerMethod.getReturnType());
    }

}
