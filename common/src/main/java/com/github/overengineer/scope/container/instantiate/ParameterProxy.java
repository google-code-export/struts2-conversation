package com.github.overengineer.scope.container.instantiate;

import com.github.overengineer.scope.container.Property;
import com.github.overengineer.scope.container.Provider;
import com.github.overengineer.scope.util.ReflectionUtil;

import java.io.Serializable;
import java.lang.annotation.Annotation;

/**
 */
public interface ParameterProxy<T> extends Serializable {

    T get(Provider provider);

    class Factory {
        public static <T> ParameterProxy<T> create(Class<T> type, Annotation[] annotations) {
            for (Annotation annotation : annotations) {
                if (annotation instanceof Property) {
                    return new PropertyParameterProxy<T>(type, ((Property) annotation).value());
                }
            }
            if (ReflectionUtil.isPropertyType(type)) {
                throw new RuntimeException("the property of type [" + type.getName() + "] should be accompanied with the @Property annotation");
            }
            return new ComponentParameterProxy<T>(type);
        }
    }

}
