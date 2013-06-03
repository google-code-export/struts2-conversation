package com.github.overengineer.container.instantiate;

import com.github.overengineer.container.Provider;
import com.github.overengineer.container.inject.InjectionException;
import com.github.overengineer.container.parameter.ParameterBuilder;
import com.github.overengineer.container.parameter.ParameterBuilderFactory;

import java.lang.ref.SoftReference;
import java.lang.reflect.Constructor;

/**
 * @author rees.byars
 */
public class DefaultInstantiator<T> implements Instantiator<T> {

    private final Class<T> type;
    private final ConstructorResolver constructorResolver;
    private final ParameterBuilderFactory parameterBuilderFactory;
    private final Class[] trailingArgsTypes;
    private ParameterBuilder<T> parameterBuilder;
    private transient volatile SoftReference<Constructor<T>> constructorRef;

    public DefaultInstantiator(Class<T> type, ConstructorResolver constructorResolver, ParameterBuilderFactory parameterBuilderFactory, Class ... trailingArgTypes) {
        this.type = type;
        this.constructorResolver = constructorResolver;
        this.parameterBuilderFactory = parameterBuilderFactory;
        this.trailingArgsTypes = trailingArgTypes;
    }

    @Override
    public boolean isDecorator() {
        //TODO cleanup
        if (parameterBuilder == null) {
            synchronized (this) {
                Constructor<T> constructor = constructorRef == null ? null : constructorRef.get();
                if (constructor == null) {
                    constructor = constructorResolver.resolveConstructor(type, trailingArgsTypes);
                    constructorRef = new SoftReference<Constructor<T>>(constructor);
                    parameterBuilder = parameterBuilderFactory.create(type, constructor, trailingArgsTypes);
                }
            }
        }
        return parameterBuilder.isDecorator();
    }

    @Override
    public T getInstance(Provider provider, Object ... trailingParams) {
        Constructor<T> constructor = constructorRef == null ? null : constructorRef.get();
        if (constructor == null) {
            synchronized (this) {
                constructor = constructorRef == null ? null : constructorRef.get();
                if (constructor == null) {
                    constructor = constructorResolver.resolveConstructor(type, trailingArgsTypes);
                    constructorRef = new SoftReference<Constructor<T>>(constructor);
                    parameterBuilder = parameterBuilderFactory.create(type, constructor, trailingArgsTypes);
                }
            }
        }
        try {
            return constructor.newInstance(parameterBuilder.buildParameters(provider, trailingParams));
        } catch (Exception e) {
            throw new InjectionException("Could not create new instance of type [" + type.getName() + "]", e);
        }
    }

    @Override
    public Class getProducedType() {
        return type;
    }
}
