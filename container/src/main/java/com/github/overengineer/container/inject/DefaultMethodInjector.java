package com.github.overengineer.container.inject;

import com.github.overengineer.container.Provider;
import com.github.overengineer.container.parameter.ParameterBuilder;

import java.lang.ref.SoftReference;
import java.lang.reflect.Method;

/**
 * @author rees.byars
 */
public class DefaultMethodInjector<T> implements MethodInjector<T> {

    private transient volatile SoftReference<Method> methodRef;
    private final String methodName;
    private final Class<?> methodDeclarer;
    private final Class[] parameterTypes;
    private final ParameterBuilder parameterBuilder;

    public DefaultMethodInjector(Method method, ParameterBuilder<T> parameterBuilder) {
        method.setAccessible(true);
        methodRef = new SoftReference<Method>(method);
        methodName = method.getName();
        methodDeclarer = method.getDeclaringClass();
        parameterTypes = method.getParameterTypes();
        this.parameterBuilder = parameterBuilder;
    }

    protected Method getMethod() {
        Method method = methodRef == null ? null : methodRef.get();
        if (method == null) {
            synchronized (this) {
                method = methodRef == null ? null : methodRef.get();
                if (method == null) {
                    try {
                        method = methodDeclarer.getDeclaredMethod(methodName, parameterTypes);
                        method.setAccessible(true);
                        methodRef = new SoftReference<Method>(method);
                    } catch (NoSuchMethodException e) {
                        throw new RuntimeException(e);
                    }
                }
            }
        }
        return method;
    }

    @Override
    public Object inject(T component, Provider provider, Object ... trailingArgs) {
        try {
            return getMethod().invoke(component, parameterBuilder.buildParameters(provider, trailingArgs));
        } catch (Exception e) {
            throw new InjectionException("Could not inject method [" + methodName + "] on component of type [" + component.getClass().getName() + "].", e);
        }
    }

}
