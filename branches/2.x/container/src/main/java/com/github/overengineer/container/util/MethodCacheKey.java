package com.github.overengineer.container.util;

import java.io.Serializable;
import java.lang.reflect.Method;

/**
 * @author rees.byars
 */
public class MethodCacheKey implements Serializable {

    private final MethodRef methodRef;
    private final int hash;

    public MethodCacheKey(Method method) {
        methodRef = new MethodRef(method);
        hash = method.hashCode();
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof MethodCacheKey)) {
            return false;
        }
        MethodCacheKey otherKey = (MethodCacheKey) other;
        return methodRef.getMethod().equals(otherKey.methodRef.getMethod());
    }

    @Override
    public int hashCode() {
        return hash;
    }
}
