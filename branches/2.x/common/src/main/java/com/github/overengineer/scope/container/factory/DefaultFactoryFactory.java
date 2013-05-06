package com.github.overengineer.scope.container.factory;

import com.github.overengineer.scope.container.Provider;
import com.github.overengineer.scope.container.key.Key;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

/**
 * @author rees.byars
 */
public class DefaultFactoryFactory implements FactoryFactory {

    @Override
    @SuppressWarnings("unchecked")
    public <T> T createFactory(final Class<T> factoryInterface, final Key producedTypeKey, final Provider provider) {
        DynamicFactory<T> dynamicFactory = new DynamicFactory<T>(factoryInterface, producedTypeKey, provider);
        T proxy = (T) Proxy.newProxyInstance(
                provider.getClass().getClassLoader(),
                new Class[]{factoryInterface},
                dynamicFactory
        );
        dynamicFactory.proxy = proxy;
        return proxy;
    }

    static class DynamicFactory<T> implements InvocationHandler  {

        private final Class<T> factoryInterface;
        private final Key producedTypeKey;
        private final Provider provider;
        T proxy;

        DynamicFactory(Class<T> factoryInterface, Key producedTypeKey, Provider provider) {
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
                return proxy.getClass().getName() + '@' + Integer.toHexString(System.identityHashCode(this));
            }
            return provider.get(producedTypeKey);
        }

    }

}
