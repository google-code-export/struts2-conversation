package com.github.overengineer.container.key;

import java.lang.reflect.Type;

/**
 * @author rees.byars
 */
public class ClassKey<T> implements Key<T> {

    private final Object qualifier;
    private final Class<T> targetClass;
    private final int hash;

    public ClassKey(Class<T> targetClass) {
        this.qualifier = Qualifier.NONE;
        this.targetClass = targetClass;
        this.hash = targetClass.hashCode();
    }

    public ClassKey(Class<T> targetClass, Object qualifier) {
        this.qualifier = qualifier == null ? Qualifier.NONE : qualifier;
        this.targetClass = targetClass;
        this.hash = targetClass.hashCode();
    }

    @Override
    public Object getQualifier() {
        return qualifier;
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
        return object instanceof Key && targetClass == ((Key) object).getType();
    }

}
