package com.github.overengineer.container.key;

import com.github.overengineer.container.util.ParameterRef;

import java.lang.reflect.Type;

/**
 * @author rees.byars
 */
public class ParameterKey<T> implements Key<T> {

    private final Object qualifier;
    private final ParameterRef parameterRef;
    private final Class<? super T> targetClass;
    private final int hash;

    public ParameterKey(ParameterRef parameterRef) {
        this.parameterRef = parameterRef;
        qualifier = Qualifier.NONE;
        Type type = parameterRef.getType();
        targetClass = KeyUtil.getClass(type);
        hash = type.hashCode();
    }

    public ParameterKey(ParameterRef parameterRef, Object qualifier) {
        this.parameterRef = parameterRef;
        this.qualifier = qualifier;
        Type type = parameterRef.getType();
        targetClass = KeyUtil.getClass(type);
        hash = type.hashCode();
    }

    @Override
    public int hashCode() {
        return hash;
    }

    @Override
    public Object getQualifier() {
        return qualifier;
    }

    @Override
    public Type getType() {
        return parameterRef.getType();
    }

    @Override
    public Class<? super T> getTargetClass() {
        return targetClass;
    }

    @Override
    public boolean equals(Object object) {
        return object instanceof Key && parameterRef.getType().equals(((Key) object).getType());
    }

}
