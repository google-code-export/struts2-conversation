package com.github.overengineer.container.key;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Set;

/**
 * @author rees.byars
 */
public class KeyUtil {

    @SuppressWarnings("unchecked")
    public static <T> Class<? super T> getClass(Type type) {
        if (type instanceof Class) {
            return (Class) type;
        } else if (type instanceof ParameterizedType) {
            return (Class) ((ParameterizedType) type).getRawType();
        } else {
            throw new UnsupportedOperationException("The type [" + type + "] is currently unsupported");
        }
    }

}
