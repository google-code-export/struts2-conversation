package com.github.overengineer.container.instantiate;

import com.github.overengineer.container.Provider;
import com.github.overengineer.container.metadata.DefaultMetadataAdapter;

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

            String propertyName = new DefaultMetadataAdapter().getPropertyName(type, annotations);

            if (propertyName == null) {
                return new ComponentParameterProxy<T>(type);
            }

            if (type instanceof ParameterizedType) {
                return new PropertyParameterProxy<T>((Class) ((ParameterizedType) type).getRawType(), propertyName);
            }

            return new PropertyParameterProxy<T>((Class) type, propertyName);

        }
    }

}
