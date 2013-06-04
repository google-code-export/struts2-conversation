package com.github.overengineer.container.key;

import java.lang.reflect.Type;

/**
 * @author rees.byars
 */
public class TempKey<T> implements Key<T> {

    private final String name;
    private final Type type;
    private final Class<? super T> targetClass;
    private final int hash;

    public TempKey(Type type, String name) {
        this.type = type;
        targetClass = KeyUtil.getClass(type);
        this.name = name == null ? "" : name;
        this.hash = type.hashCode();
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
    public Class<? super T> getTargetClass() {
        return targetClass;
    }

    @Override
    public int hashCode() {
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        return object instanceof Key && type.equals(((Key) object).getType()) && name.equals(((Key) object).getName());
    }
}
