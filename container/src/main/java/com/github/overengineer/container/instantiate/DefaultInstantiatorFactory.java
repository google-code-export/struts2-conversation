package com.github.overengineer.container.instantiate;

import com.github.overengineer.container.parameter.ParameterBuilderFactory;

/**
 * @author rees.byars
 */
public class DefaultInstantiatorFactory implements InstantiatorFactory {

    private final ConstructorResolver constructorResolver;
    private final ParameterBuilderFactory parameterBuilderFactory;

    public DefaultInstantiatorFactory(ConstructorResolver constructorResolver, ParameterBuilderFactory parameterBuilderFactory) {
        this.constructorResolver = constructorResolver;
        this.parameterBuilderFactory = parameterBuilderFactory;
    }

    @Override
    public <T> Instantiator<T> create(Class<T> implementationType, Class ... trailingParamTypes) {
        return new DefaultInstantiator<T>(implementationType, constructorResolver, parameterBuilderFactory, trailingParamTypes);
    }

}
