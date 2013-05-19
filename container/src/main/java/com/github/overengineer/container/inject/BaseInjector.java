package com.github.overengineer.container.inject;

import java.lang.ref.SoftReference;
import java.lang.reflect.Method;

/**
 * @author rees.byars
 */
public abstract class BaseInjector<T> implements Injector<T> {

    private transient volatile SoftReference<Method> setterRef;
    private final String setterName;
    private final Class<?> setterDeclarer;
    protected final Class<?> parameterType;

    public BaseInjector(Method setter, Class parameterType) {
        setterRef = new SoftReference<Method>(setter);
        this.parameterType = parameterType;
        setterName = setter.getName();
        setterDeclarer = setter.getDeclaringClass();
    }

    protected Method getSetter() {
        Method setter = setterRef == null ? null : setterRef.get();
        if (setter == null) {
            synchronized (this) {
                setter = setterRef == null ? null : setterRef.get();
                if (setter == null) {
                    try {
                        setter = setterDeclarer.getDeclaredMethod(setterName, parameterType);
                        setterRef = new SoftReference<Method>(setter);
                    } catch (NoSuchMethodException e) {
                        throw new RuntimeException(e);
                    }
                }
            }
        }
        return setter;
    }

}
