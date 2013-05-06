package com.github.overengineer.scope.container.type;

import java.lang.reflect.Type;

/**
 * @author rees.byars
 */
public class ClassKey implements SerializableKey {

    private final Class targetClass;

    public ClassKey(Class targetClass) {
        this.targetClass = targetClass;
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
        return targetClass.hashCode();
    }

    @Override
    public boolean equals(Object object) {
        return object instanceof Key && targetClass.equals(((Key) object).getType());
    }

}
