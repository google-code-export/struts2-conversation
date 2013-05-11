package com.github.overengineer.container.instantiate;

import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.Type;

/**
 * @author rees.byars
 */
public class DefaultConstructorResolver implements ConstructorResolver {

    @Override
    @SuppressWarnings("unchecked")
    public <T> Constructor<T> resolveConstructor(Class<T> type, Callback callback) {
        Type[] genericParameterTypes = {};
        Annotation[][] annotations = {};
        Constructor<T> constructor = null;
        for (Constructor candidateConstructor : type.getDeclaredConstructors()) {
            Type[] candidateTypes = candidateConstructor.getGenericParameterTypes();
            if (candidateTypes.length >= genericParameterTypes.length) {
                constructor = candidateConstructor;
                genericParameterTypes = candidateTypes;
                annotations = constructor.getParameterAnnotations();
            }
        }
        assert constructor != null;
        constructor.setAccessible(true);
        callback.onResolution(genericParameterTypes, annotations);
        return constructor;
    }

}
