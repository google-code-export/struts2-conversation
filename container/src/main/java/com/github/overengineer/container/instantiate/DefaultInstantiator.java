package com.github.overengineer.container.instantiate;

import com.github.overengineer.container.Provider;
import com.github.overengineer.container.inject.InjectionException;

import java.lang.reflect.Constructor;

/**
 * @author rees.byars
 */
public class DefaultInstantiator<T> implements Instantiator<T> {

    private final Class<T> type;
    private final ConstructorResolver constructorResolver;
    private final ParameterProxyProvider parameterProxyProvider;
    private transient Constructor<T> constructor;
    private transient Object[] parameters;

    public DefaultInstantiator(Class<T> type, ConstructorResolver constructorResolver, ParameterProxyProvider parameterProxyProvider) {
        this.type = type;
        this.constructorResolver = constructorResolver;
        this.parameterProxyProvider = parameterProxyProvider;
    }

    @Override
    public T getInstance(Provider provider) {
        if (constructor == null) {
            constructor = constructorResolver.resolveConstructor(type, parameterProxyProvider);
            parameters = new Object[parameterProxyProvider.getParameterProxies().length];
        }
        ParameterProxy[] parameterProxies = parameterProxyProvider.getParameterProxies();
        try {
            for (int i = 0; i < parameterProxies.length; i++) {
                parameters[i] = parameterProxies[i].get(provider);
            }
            return constructor.newInstance(parameters);
        } catch (Exception e) {
            throw new InjectionException("Could not create new instance of type [" + type.getName() + "]", e);
        }
    }
}
