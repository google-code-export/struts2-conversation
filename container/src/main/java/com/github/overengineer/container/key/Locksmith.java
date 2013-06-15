package com.github.overengineer.container.key;

import com.github.overengineer.container.util.ParameterRef;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

/**
 * @author rees.byars
 */
public abstract class Locksmith {

    private Locksmith(){}

    public static <T> Key<T> makeKey(Class<T> cls) {
        return new ClassKey<T>(cls);
    }

    public static <T> Key<T> makeKey(Class<T> cls, Object qualifier) {
        return new ClassKey<T>(cls, qualifier);
    }

    public static <T> Key<T> makeKey(ParameterRef parameterRef) {
        return makeKey(parameterRef, null);
    }

    @SuppressWarnings("unchecked")
    public static  <T> Key<T> makeKey(ParameterRef parameterRef, Object qualifier) {
        Type type = parameterRef.getType();
        if (type instanceof Class) {
            return makeKey((Class<T>) type, qualifier);
        }
        if (type instanceof ParameterizedType) {
            return new ParameterKey<T>(parameterRef, qualifier);
        }
        throw new UnsupportedOperationException("Unsupported injection type [" + type + "]");
    }


}
