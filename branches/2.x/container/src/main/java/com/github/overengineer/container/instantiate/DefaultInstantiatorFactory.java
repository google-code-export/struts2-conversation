package com.github.overengineer.container.instantiate;

import com.github.overengineer.container.ComponentStrategy;

/**
 * @author rees.byars
 */
public class DefaultInstantiatorFactory implements InstantiatorFactory {

    private final ConstructorResolver constructorResolver;
    private final ParameterProxyFactory parameterProxyFactory;

    public DefaultInstantiatorFactory(ConstructorResolver constructorResolver, ParameterProxyFactory parameterProxyFactory) {
        this.constructorResolver = constructorResolver;
        this.parameterProxyFactory = parameterProxyFactory;
    }

    @Override
    public <T> Instantiator<T> create(Class<T> implementationType, Class ... trailingParamTypes) {
        return new DefaultInstantiator<T>(implementationType, constructorResolver, new DefaultParameterProvider(parameterProxyFactory), trailingParamTypes);
    }

    @Override
    public <T> Instantiator<T> create(Class<T> implementationType, Class<?> delegateClass, ComponentStrategy<?> delegateStrategy) {
        return new DefaultInstantiator<T>(implementationType, constructorResolver, new DecoratorParameterProvider(parameterProxyFactory, delegateClass, delegateStrategy));
    }

}
