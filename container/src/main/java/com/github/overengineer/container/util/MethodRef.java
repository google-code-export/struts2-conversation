package com.github.overengineer.container.util;

import java.io.Serializable;
import java.lang.ref.SoftReference;
import java.lang.reflect.Method;

/**
 * @author rees.byars
 */
public class MethodRef implements Serializable {

    private transient volatile SoftReference<Method> methodRef;
    private final String methodName;
    private final Class<?> methodDeclarer;
    private final Class[] parameterTypes;

    public MethodRef(Method method) {
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
}
