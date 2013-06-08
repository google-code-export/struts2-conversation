package com.github.overengineer.container.inject;

import com.github.overengineer.container.Provider;
import com.github.overengineer.container.parameter.ParameterBuilder;
import com.github.overengineer.container.util.MethodRef;

import java.lang.reflect.Method;

/**
 * @author rees.byars
 */
public class DefaultMethodInjector<T> implements MethodInjector<T> {

    private final MethodRef methodRef;
    private final ParameterBuilder parameterBuilder;

    public DefaultMethodInjector(Method method, ParameterBuilder<T> parameterBuilder) {
        methodRef = new MethodRef(method);
        this.parameterBuilder = parameterBuilder;
    }

    @Override
    public Object inject(T component, Provider provider, Object ... trailingArgs) {
        try {
            return methodRef.getMethod().invoke(component, parameterBuilder.buildParameters(provider, trailingArgs));
        } catch (Exception e) {
            throw new InjectionException("Could not inject method [" + methodRef.getMethod().getName() + "] on component of type [" + component.getClass().getName() + "].", e);
        }
    }

}
