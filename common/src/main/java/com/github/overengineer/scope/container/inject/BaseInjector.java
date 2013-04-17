package com.github.overengineer.scope.container.inject;

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
    protected final Class<?> type;

    public BaseInjector(Method setter, Class<?> type) {
        this.setter = setter;
        setterName = setter.getName();
        setterDeclarer = setter.getDeclaringClass();
        this.type = type;
    }

    private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
        in.defaultReadObject();
        try {
            setter = setterDeclarer.getDeclaredMethod(setterName, type);
        } catch (NoSuchMethodException e) {
            throw new RuntimeException(e);
        }
    }

}
