package com.github.overengineer.scope.container.instantiate;

import com.github.overengineer.scope.container.Property;
import com.github.overengineer.scope.container.Provider;
import com.github.overengineer.scope.util.ReflectionUtil;

import java.io.Serializable;
import java.lang.annotation.Annotation;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

/**
 * @author rees.byars
 */
public interface ParameterProxy<T> extends Serializable {

    T get(Provider provider);

    class Factory {
        @SuppressWarnings("unchecked")
        public static <T> ParameterProxy<T> create(Type type, Annotation[] annotations) {
            for (Annotation annotation : annotations) {
                //TODO
                if (annotation instanceof Property) {
                    if (type instanceof ParameterizedType) {
                        return new PropertyParameterProxy<T>((Class) ((ParameterizedType) type).getRawType(), ((Property) annotation).value());
                    }
                    return new PropertyParameterProxy<T>((Class) type, ((Property) annotation).value());
                }
            }
            return new ComponentParameterProxy<T>(type);
        }
    }

}
