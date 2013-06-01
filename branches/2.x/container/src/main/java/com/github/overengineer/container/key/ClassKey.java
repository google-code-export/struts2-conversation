package com.github.overengineer.container.key;

import java.lang.reflect.Type;

/**
 * @author rees.byars
 */
public class ClassKey implements SerializableKey {

    private final String name;
    private final Class targetClass;
    private final int hash;

    public ClassKey(Class targetClass) {
        this.name = targetClass.getName();
        this.targetClass = targetClass;
        this.hash = targetClass.hashCode() * 31 + name.hashCode();
    }

    public ClassKey(Class targetClass, String name) {
        this.name = name;
        this.targetClass = targetClass;
        this.hash = targetClass.hashCode() * 31 + name.hashCode();
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
    public Class getTargetClass() {
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
