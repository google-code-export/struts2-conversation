package com.github.overengineer.container.key;

import java.lang.reflect.Type;

/**
 * @author rees.byars
 */
public class TempKey implements Key {

    private final String name;
    private final Type type;
    private final Class targetClass;
    private final int hash;

    public TempKey(Type type, String name) {
        this.type = type;
        targetClass = KeyUtil.getClass(type);
        this.name = name;
        this.hash = type.hashCode() * 31 + name.hashCode();
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
    public int hashCode() {
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        return object instanceof Key && type.equals(((Key) object).getType()) && name.equals(((Key) object).getName());
    }
}
