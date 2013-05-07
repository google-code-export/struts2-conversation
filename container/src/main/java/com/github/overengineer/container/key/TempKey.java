package com.github.overengineer.container.key;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

/**
 * @author rees.byars
 */
public class TempKey implements Key {

    private final Type type;
    private final Class targetClass;

    public TempKey(Type type) {
        this.type = type;
        if (type instanceof Class) {
            targetClass = (Class) type;
        } else if (type instanceof ParameterizedType) {
            targetClass = (Class) ((ParameterizedType) type).getRawType();
        } else {
            throw new UnsupportedOperationException("The type [" + type + "] is currently unsupported");
        }
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
    public int hashCode() {
        return type.hashCode();
    }

    @Override
    public boolean equals(Object object) {
        return object instanceof Key && type.equals(((Key) object).getType());
    }
}
