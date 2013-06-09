package com.github.overengineer.container.dynamic;

import com.github.overengineer.container.Provider;
import com.github.overengineer.container.inject.InjectorFactory;
import com.github.overengineer.container.instantiate.InstantiatorFactory;
import com.github.overengineer.container.key.Key;
import com.github.overengineer.container.metadata.MetadataAdapter;

import java.io.Serializable;
import java.lang.reflect.Proxy;

/**
 * @author rees.byars
 */
public class DefaultDynamicComponentFactory implements DynamicComponentFactory {

    private final InstantiatorFactory instantiatorFactory;
    private final InjectorFactory injectorFactory;
    private final MetadataAdapter metadataAdapter;

    public DefaultDynamicComponentFactory(InstantiatorFactory instantiatorFactory, InjectorFactory injectorFactory, MetadataAdapter metadataAdapter) {
        this.instantiatorFactory = instantiatorFactory;
        this.injectorFactory = injectorFactory;
        this.metadataAdapter = metadataAdapter;
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> T createManagedComponentFactory(final Class factoryInterface, final Key producedTypeKey, final Provider provider) {
        DynamicManagedComponentFactory<T> dynamicFactory = new DynamicManagedComponentFactory<T>(factoryInterface, producedTypeKey, provider);
        T proxy = (T) Proxy.newProxyInstance(
                provider.getClass().getClassLoader(),
                new Class[]{factoryInterface, Serializable.class},
                dynamicFactory
        );
        dynamicFactory.proxy = proxy;
        return proxy;
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> T createNonManagedComponentFactory(Class factoryInterface, Class concreteProducedType, Provider provider) {
        DynamicNonManagedComponentFactory<T> dynamicFactory = new DynamicNonManagedComponentFactory<T>(
                factoryInterface,
                concreteProducedType,
                provider,
                instantiatorFactory.create(
                        concreteProducedType,
                        factoryInterface.getDeclaredMethods()[0].getParameterTypes()));
        T proxy = (T) Proxy.newProxyInstance(
                provider.getClass().getClassLoader(),
                new Class[]{factoryInterface, Serializable.class},
                dynamicFactory
        );
        dynamicFactory.proxy = proxy;
        return proxy;
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> T createCompositeHandler(Class<T> targetInterface, final Provider provider) {
        DynamicComposite<T> handler = new DynamicComposite<T>(targetInterface, provider);
        handler.proxy = (T) Proxy.newProxyInstance(
                this.getClass().getClassLoader(),
                new Class[]{targetInterface, Serializable.class},
                handler
        );
        return handler.proxy;
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> T createDelegatingService(Class<T> serviceInterface, Provider provider) {
        DelegatingService<T> delegatingService = new DelegatingService<T>(serviceInterface, provider, injectorFactory, metadataAdapter);
        delegatingService.proxy = (T) Proxy.newProxyInstance(
                this.getClass().getClassLoader(),
                new Class[]{serviceInterface, Serializable.class},
                delegatingService
        );
        return delegatingService.proxy;
    }


}
