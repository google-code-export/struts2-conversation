package com.github.overengineer.container.util;

import java.lang.annotation.Annotation;
import java.lang.ref.SoftReference;
import java.lang.reflect.Constructor;
import java.lang.reflect.Type;

/**
 * @author rees.byars
 */
public class ConstructorRefImpl<T> implements ConstructorRef<T> {

    private transient volatile SoftReference<Constructor<T>> constructorRef;
    private final Class<T> constructorDeclarer;
    private final Class[] parameterTypes;

    public ConstructorRefImpl(Constructor<T> constructor) {
        constructor.setAccessible(true);
        constructorRef = new SoftReference<Constructor<T>>(constructor);
        constructorDeclarer = constructor.getDeclaringClass();
        parameterTypes = constructor.getParameterTypes();
    }

    public Constructor<T> getConstructor() {
        Constructor<T> constructor = constructorRef == null ? null : constructorRef.get();
        if (constructor == null) {
            synchronized (this) {
                constructor = constructorRef == null ? null : constructorRef.get();
                if (constructor == null) {
                    try {
                        constructor = constructorDeclarer.getConstructor(parameterTypes);
                        constructor.setAccessible(true);
                        constructorRef = new SoftReference<Constructor<T>>(constructor);
                    } catch (NoSuchMethodException e) {
                        throw new RuntimeException(e);
                    }
                }
            }
        }
        return constructor;
    }

    @Override
    public Type[] getParameterTypes() {
        return getConstructor().getGenericParameterTypes();
    }

    @Override
    public Annotation[][] getParameterAnnotations() {
        return getConstructor().getParameterAnnotations();
    }
}
