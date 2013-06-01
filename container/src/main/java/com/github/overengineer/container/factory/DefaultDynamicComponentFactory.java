package com.github.overengineer.container.factory;

import com.github.overengineer.container.Provider;
import com.github.overengineer.container.instantiate.Instantiator;
import com.github.overengineer.container.instantiate.InstantiatorFactory;
import com.github.overengineer.container.key.SerializableKey;

import java.io.Serializable;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.List;

/**
 * @author rees.byars
 */
public class DefaultDynamicComponentFactory implements DynamicComponentFactory {

    private InstantiatorFactory instantiatorFactory;

    public DefaultDynamicComponentFactory(InstantiatorFactory instantiatorFactory) {
        this.instantiatorFactory = instantiatorFactory;
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> T createManagedComponentFactory(final Class factoryInterface, final SerializableKey producedTypeKey, final Provider provider) {
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
    public <T> CompositeHandler<T> createCompositeHandler(Class<T> targetInterface) {
        DynamicCompositeHandler<T> handler = new DynamicCompositeHandler<T>(targetInterface);
        handler.proxy = (T) Proxy.newProxyInstance(
                this.getClass().getClassLoader(),
                new Class[]{targetInterface, Serializable.class},
                handler
        );
        return handler;
    }

    static class DynamicManagedComponentFactory<T> implements InvocationHandler, Serializable  {

        private final Class<T> factoryInterface;
        private final SerializableKey producedTypeKey;
        private final Provider provider;
        private T proxy;

        DynamicManagedComponentFactory(Class<T> factoryInterface, SerializableKey producedTypeKey, Provider provider) {
            this.factoryInterface = factoryInterface;
            this.producedTypeKey = producedTypeKey;
            this.provider = provider;
        }

        @Override
        public Object invoke(Object o, Method method, Object[] objects) throws Throwable {
            String methodName = method.getName();
            if ("equals".equals(methodName)) {
                return proxy == objects[0];
            } else if ("hashCode".equals(methodName)) {
                return System.identityHashCode(proxy);
            } else if ("toString".equals(methodName)) {
                return proxy.getClass().getName() + '@' + Integer.toHexString(System.identityHashCode(this)) + "$DynamicManagedComponentFactory$[" + factoryInterface + "][" + producedTypeKey.getType() + "]";
            }
            return provider.get(producedTypeKey);
        }

    }

    static class DynamicNonManagedComponentFactory<T> implements InvocationHandler, Serializable  {

        private final Class<T> factoryInterface;
        private final Class concreteProducedType;
        private final Provider provider;
        private final Instantiator instantiator;
        private T proxy;

        DynamicNonManagedComponentFactory(Class<T> factoryInterface, Class concreteProducedType, Provider provider, Instantiator instantiator) {
            this.factoryInterface = factoryInterface;
            this.concreteProducedType = concreteProducedType;
            this.provider = provider;
            this.instantiator = instantiator;
        }

        @Override
        public Object invoke(Object o, Method method, Object[] objects) throws Throwable {
            String methodName = method.getName();
            if ("equals".equals(methodName)) {
                return proxy == objects[0];
            } else if ("hashCode".equals(methodName)) {
                return System.identityHashCode(proxy);
            } else if ("toString".equals(methodName)) {
                return proxy.getClass().getName() + '@' + Integer.toHexString(System.identityHashCode(this)) + "$DynamicNonManagedComponentFactory$[" + factoryInterface.getName() + "][" + concreteProducedType.getName() + "]" ;
            }
            return instantiator.getInstance(provider, objects);
        }

    }

    static class DynamicCompositeHandler<T> implements InvocationHandler, CompositeHandler<T> {

        private List<T> components = new ArrayList<T>();
        private T proxy;
        private Class<T> componentInterface;

        DynamicCompositeHandler(Class<T> componentInterface) {
            this.componentInterface = componentInterface;
        }

        @Override
        public Object invoke(Object o, Method method, Object[] objects) throws Throwable {
            String methodName = method.getName();
            if ("equals".equals(methodName)) {
                return proxy == objects[0];
            } else if ("hashCode".equals(methodName)) {
                return System.identityHashCode(proxy);
            } else if ("toString".equals(methodName)) {
                return proxy.getClass().getName() + '@' + Integer.toHexString(System.identityHashCode(this)) + "$DynamicComposite$[" + componentInterface.getName() + "]";
            }
            for (T component : components) {
                method.invoke(component, objects);
            }
            return null;
        }

        @Override
        public T getComposite() {
            return proxy;
        }

        @Override
        public CompositeHandler<T> add(T component) {
            components.add(component);
            return this;
        }

    }

}
