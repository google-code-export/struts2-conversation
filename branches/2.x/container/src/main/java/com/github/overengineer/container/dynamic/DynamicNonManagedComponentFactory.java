package com.github.overengineer.container.dynamic;

import com.github.overengineer.container.Provider;
import com.github.overengineer.container.instantiate.Instantiator;

import java.io.Serializable;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

/**
 * @author rees.byars
 */
public class DynamicNonManagedComponentFactory<T> implements InvocationHandler, Serializable {

    private final Class<T> factoryInterface;
    private final Class concreteProducedType;
    private final Provider provider;
    private final Instantiator instantiator;
    T proxy;

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
