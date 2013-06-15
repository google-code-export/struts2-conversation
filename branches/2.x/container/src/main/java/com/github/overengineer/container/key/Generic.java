package com.github.overengineer.container.key;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

/**
 *
 * @author rees.byars
 */
public abstract class Generic<T> implements Key<T> {

    private final Object qualifier;
    private transient Type type;
    private transient Class<? super T> targetClass;
    private final int hash;

    public Generic() {
        init();
        this.qualifier = Qualifier.NONE;
        this.hash = type.hashCode();
    }

    public Generic(Object qualifier) {
        init();
        this.qualifier = qualifier;
        this.hash = type.hashCode();
    }

    private void init() {
        ParameterizedType parameterizedType = (ParameterizedType) getClass().getGenericSuperclass();
        type = parameterizedType.getActualTypeArguments()[0];
        if (type instanceof ParameterizedType) {
            targetClass = KeyUtil.getClass(type);
        } else {
            throw new UnsupportedOperationException("The GenericKey is invalid [" + type + "]");
        }
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
        return type;
    }

    @Override
    public Class<? super T> getTargetClass() {
        return targetClass;
    }

    @Override
    public boolean equals(Object object) {
        return object instanceof Key && type.equals(((Key) object).getType());
    }

    private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
        in.defaultReadObject();
        this.init();
    }

}
