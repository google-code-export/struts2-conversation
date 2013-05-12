package com.github.overengineer.container.inject;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.lang.reflect.Method;

/**
 * @author rees.byars
 */
public abstract class BaseInjector<T> implements Injector<T> {

    protected transient Method setter;
    private final String setterName;
    private final Class<?> setterDeclarer;
    protected final Class parameterType;

    public BaseInjector(Method setter, Class parameterType) {
        this.setter = setter;
        this.parameterType = parameterType;
        setterName = setter.getName();
        setterDeclarer = setter.getDeclaringClass();
    }

    private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
        in.defaultReadObject();
        try {
            setter = setterDeclarer.getDeclaredMethod(setterName, parameterType);
        } catch (NoSuchMethodException e) {
            throw new RuntimeException(e);
        }
    }

}
