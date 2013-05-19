package com.github.overengineer.container.inject;

import com.github.overengineer.container.Provider;
import com.github.overengineer.container.metadata.MetadataAdapter;
import com.github.overengineer.container.parameter.ParameterProxyFactory;

import java.lang.reflect.Method;
import java.util.HashSet;
import java.util.Set;

/**
 * @author rees.byars
 */
public class DefaultInjectorFactory implements InjectorFactory {

    private final MetadataAdapter metadataAdapter;
    private final ParameterProxyFactory parameterProxyFactory;

    public DefaultInjectorFactory(MetadataAdapter metadataAdapter, ParameterProxyFactory parameterProxyFactory) {
        this.metadataAdapter = metadataAdapter;
        this.parameterProxyFactory = parameterProxyFactory;
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> ComponentInjector<T> create(Class<T> implementationType) {
        Set<MethodInjector<T>> injectors = new HashSet<MethodInjector<T>>();
        for (Method method : implementationType.getMethods()) {
            if (metadataAdapter.isSetter(method)) {
                MethodInjector<T> setterInjector = create(method);
                injectors.add(setterInjector);
            }
        }
        if (injectors.size() == 0) {
            return new EmptyInjector<T>();
        } else {
            return new DefaultComponentInjector<T>(injectors);
        }
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> MethodInjector<T> create(Method method) {
        return new DefaultMethodInjector(method, parameterProxyFactory.create(method));
    }

    static class EmptyInjector<T> implements ComponentInjector<T> {
        @Override
        public void inject(T component, Provider provider) {}
    }
}
