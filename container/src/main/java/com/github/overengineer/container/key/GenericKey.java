package com.github.overengineer.container.key;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

/**
 *
 * @author rees.byars
 */
public abstract class GenericKey<T> implements SerializableKey {

    private final String name;
    private transient Type type;
    private transient Class targetClass;
    private final int hash;

    public GenericKey() {
        init();
        this.name = targetClass.getName();
        this.hash = type.hashCode() * 31 + name.hashCode();
    }

    public GenericKey(String name) {
        init();
        this.name = name;
        this.hash = type.hashCode() * 31 + name.hashCode();
    }

    private void init() {
        ParameterizedType parameterizedType = (ParameterizedType) getClass().getGenericSuperclass();
        type = parameterizedType.getActualTypeArguments()[0];
        if (type instanceof ParameterizedType) {
            targetClass = (Class) ((ParameterizedType) type).getRawType();
        } else {
            throw new UnsupportedOperationException("The GenericKey is invalid [" + type + "]");
        }
    }

    @Override
    public int hashCode() {
        return hash;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public Type getType() {
        return type;
    }

    @Override
    public Class getTargetClass() {
        return targetClass;
    }

    @Override
    public boolean equals(Object object) {
        return object instanceof Key && type.equals(((Key) object).getType()) && name.equals(((Key) object).getName());
    }

    private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
        in.defaultReadObject();
        this.init();
    }

}