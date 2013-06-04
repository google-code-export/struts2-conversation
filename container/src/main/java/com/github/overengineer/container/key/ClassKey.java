package com.github.overengineer.container.key;

import java.lang.reflect.Type;

/**
 * @author rees.byars
 */
public class ClassKey<T> implements SerializableKey<T> {

    private final String name;
    private final Class<T> targetClass;
    private final int hash;

    public ClassKey(Class<T> targetClass) {
        this.name = "";
        this.targetClass = targetClass;
        this.hash = targetClass.hashCode();
    }

    public ClassKey(Class<T> targetClass, String name) {
        this.name = name == null ? "" : name;
        this.targetClass = targetClass;
        this.hash = targetClass.hashCode();
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public Type getType() {
        return targetClass;
    }

    @Override
    public Class<T> getTargetClass() {
        return targetClass;
    }

    @Override
    public int hashCode() {
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        return object instanceof Key && targetClass == ((Key) object).getType() && name.equals(((Key) object).getName());
    }

}
