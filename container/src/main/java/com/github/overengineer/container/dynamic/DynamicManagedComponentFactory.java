package com.github.overengineer.container.dynamic;

import com.github.overengineer.container.Provider;
import com.github.overengineer.container.key.Key;

import java.io.Serializable;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

/**
 * @author rees.byars
 */
public class DynamicManagedComponentFactory<T> implements InvocationHandler, Serializable {

    private final Class<T> factoryInterface;
    private final Key<?> producedTypeKey;
    private final Provider provider;
    T proxy;

    DynamicManagedComponentFactory(Class<T> factoryInterface, Key producedTypeKey, Provider provider) {
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
