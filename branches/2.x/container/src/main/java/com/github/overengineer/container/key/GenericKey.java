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

    private transient Type type;
    private transient Class targetClass;

    public GenericKey() {
        init();
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
        return type.hashCode();
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
        return object instanceof Key && type.equals(((Key) object).getType());
    }

    private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
        in.defaultReadObject();
        this.init();
    }

}
