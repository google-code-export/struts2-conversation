package com.github.overengineer.scope.container.factory;

import com.github.overengineer.scope.container.Provider;
import com.github.overengineer.scope.container.type.Key;

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
        return (T) Proxy.newProxyInstance(
                provider.getClass().getClassLoader(),
                new Class[]{factoryInterface},
                new InvocationHandler() {
                    @Override
                    public Object invoke(Object o, Method method, Object[] objects) throws Throwable {
                        return provider.get(producedTypeKey);
                    }
                }
        );
    }

}
