package com.github.overengineer.container.inject;

import com.github.overengineer.container.Provider;
import com.github.overengineer.container.parameter.ParameterProxy;

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
    private final ParameterProxy[] parameterProxies;

    public DefaultMethodInjector(Method method, ParameterProxy[] parameterProxies) {
        method.setAccessible(true);
        methodRef = new SoftReference<Method>(method);
        methodName = method.getName();
        methodDeclarer = method.getDeclaringClass();
        parameterTypes = method.getParameterTypes();
        this.parameterProxies = parameterProxies;
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
            Object[] parameters = new Object[parameterProxies.length + trailingArgs.length];
            for (int i = 0; i < parameters.length; i++) {
                parameters[i] = parameterProxies[i].get(provider);
            }
            if (trailingArgs.length > 0) {
                if (parameterProxies.length > 0) {
                    System.arraycopy(trailingArgs, 0, parameters, parameterProxies.length, trailingArgs.length + parameterProxies.length - 1);
                } else {
                    parameters = trailingArgs;
                }
            }
            return getMethod().invoke(component, parameters);
        } catch (Exception e) {
            throw new InjectionException("Could not inject method [" + methodName + "] on component of type [" + component.getClass().getName() + "].", e);
        }
    }

}
