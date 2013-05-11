package com.github.overengineer.container.key;

import java.lang.reflect.Type;

/**
 * @author rees.byars
 */
public class TempKey implements Key {

    private final Type type;
    private final Class targetClass;

    public TempKey(Type type) {
        this.type = type;
        targetClass = KeyUtil.getClass(type);
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
