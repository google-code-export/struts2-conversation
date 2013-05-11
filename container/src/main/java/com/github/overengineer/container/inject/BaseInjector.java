package com.github.overengineer.container.inject;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

/**
 * @author rees.byars
 */
public abstract class BaseInjector<T> implements Injector<T> {

    protected transient Method setter;
    protected transient Type type;
    private final String setterName;
    private final Class<?> setterDeclarer;
    protected final Class<?> parameterClass;


    public BaseInjector(Method setter, Type type) {
        this.setter = setter;
        this.type = type;
        setterName = setter.getName();
        setterDeclarer = setter.getDeclaringClass();
        if (type instanceof ParameterizedType) {
            parameterClass = (Class<?>) ((ParameterizedType) type).getRawType();
        } else if (type instanceof Class) {
            parameterClass = (Class) type;
        } else {
            throw new UnsupportedOperationException();
        }
    }

    private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
        in.defaultReadObject();
        try {
            setter = setterDeclarer.getDeclaredMethod(setterName, parameterClass);
            type = setter.getGenericParameterTypes()[0];
        } catch (NoSuchMethodException e) {
            throw new RuntimeException(e);
        }
    }

}
