package com.github.overengineer.container.key;

import java.lang.reflect.Type;

/**
 * @author rees.byars
 */
public class TempKey<T> implements BasicKey<T> {

    private final Object qualifier;
    private final Type type;
    private final Class<? super T> targetClass;
    private final int hash;

    public TempKey(Type type, Object qualifier) {
        this.type = type;
        targetClass = KeyUtil.getClass(type);
        this.qualifier = qualifier == null ? Qualifier.NONE : qualifier;
        this.hash = type.hashCode();
    }

    @Override
    public Object getQualifier() {
        return qualifier;
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
        return object instanceof BasicKey && type.equals(((BasicKey) object).getType()) && qualifier.equals(((BasicKey) object).getQualifier());
    }
}
