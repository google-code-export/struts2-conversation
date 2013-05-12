package com.github.overengineer.container.key;

import java.lang.reflect.Type;

/**
 * @author rees.byars
 */
public abstract class DelegatingSerializableKey implements SerializableKey {

    protected abstract SerializableKey getDelegateKey();

    @Override
    public int hashCode() {
        return getDelegateKey().hashCode();
    }

    @Override
    public Type getType() {
        return getDelegateKey().getType();
    }

    @Override
    public Class getTargetClass() {
        return getDelegateKey().getTargetClass();
    }

    @Override
    public boolean equals(Object object) {
        return object instanceof Key && getDelegateKey().equals(object);
    }

}
