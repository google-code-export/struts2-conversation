package com.github.overengineer.container.util;

import java.lang.annotation.Annotation;
import java.lang.ref.SoftReference;
import java.lang.reflect.Method;
import java.lang.reflect.Type;

/**
 * @author rees.byars
 */
public class MethodRefImpl implements MethodRef {

    private transient volatile SoftReference<Method> methodRef;
    private final String methodName;
    private final Class<?> methodDeclarer;
    private final Class[] parameterTypes;

    public MethodRefImpl(Method method) {
        method.setAccessible(true);
        methodRef = new SoftReference<Method>(method);
        methodName = method.getName();
        methodDeclarer = method.getDeclaringClass();
        parameterTypes = method.getParameterTypes();
    }

    public Method getMethod() {
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
    public Type[] getParameterTypes() {
        return getMethod().getGenericParameterTypes();
    }

    @Override
    public Annotation[][] getParameterAnnotations() {
        return getMethod().getParameterAnnotations();
    }
}
