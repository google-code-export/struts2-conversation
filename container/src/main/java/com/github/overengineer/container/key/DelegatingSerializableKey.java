package com.github.overengineer.container.key;

import java.lang.reflect.Type;

/**
 * @author rees.byars
 */
public abstract class DelegatingSerializableKey<T> implements Key<T> {

    protected abstract Key<T> getDelegateKey();

    @Override
    public int hashCode() {
        return getDelegateKey().hashCode();
    }

    @Override
    public String getName() {
        return getDelegateKey().getName();
    }

    @Override
    public Type getType() {
        return getDelegateKey().getType();
    }

    @Override
    public Class<? super T> getTargetClass() {
        return getDelegateKey().getTargetClass();
    }

    @Override
    public boolean equals(Object object) {
        return object instanceof BasicKey && getDelegateKey().equals(object);
    }

}
