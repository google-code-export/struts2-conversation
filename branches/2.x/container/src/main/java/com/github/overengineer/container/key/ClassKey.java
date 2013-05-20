package com.github.overengineer.container.key;

import java.lang.reflect.Type;

/**
 * @author rees.byars
 */
public class ClassKey implements SerializableKey {

    private final Class targetClass;
    private final int hash;

    public ClassKey(Class targetClass) {
        this.targetClass = targetClass;
        this.hash = targetClass.hashCode();
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
        return object instanceof Key && targetClass == ((Key) object).getType();
    }

}
