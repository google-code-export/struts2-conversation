package com.github.overengineer.container.inject;

import com.github.overengineer.container.Provider;
import com.github.overengineer.container.metadata.MetadataAdapter;
import com.github.overengineer.container.parameter.ParameterBuilderFactory;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

/**
 * @author rees.byars
 */
public class DefaultInjectorFactory implements InjectorFactory {

    private final MetadataAdapter metadataAdapter;
    private final ParameterBuilderFactory parameterBuilderFactory;

    public DefaultInjectorFactory(MetadataAdapter metadataAdapter, ParameterBuilderFactory parameterBuilderFactory) {
        this.metadataAdapter = metadataAdapter;
        this.parameterBuilderFactory = parameterBuilderFactory;
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> ComponentInjector<T> create(Class<T> implementationType) {
        List<MethodInjector<T>> injectors = new ArrayList<MethodInjector<T>>();
        for (Method method : implementationType.getMethods()) {
            if (metadataAdapter.isSetter(method)) {
                MethodInjector<T> setterInjector = create(implementationType, method);
                injectors.add(setterInjector);
            }
        }
        Method initMethod = metadataAdapter.getInitMethod(implementationType);
        if (initMethod != null) {
            MethodInjector<T> initInjector = create(implementationType, initMethod);
            injectors.add(initInjector);
        }
        if (injectors.size() == 0) {
            return new EmptyInjector<T>();
        } else {
            return new DefaultComponentInjector<T>(injectors);
        }
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> MethodInjector<T> create(Class<T> injectionTarget, Method method, Class ... trailingArgs) {
        return new DefaultMethodInjector(method, parameterBuilderFactory.create(injectionTarget, method, trailingArgs));
    }

    static class EmptyInjector<T> implements ComponentInjector<T> {
        @Override
        public void inject(T component, Provider provider) {}
    }
}
