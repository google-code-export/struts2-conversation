package com.github.overengineer.container.instantiate;

import com.github.overengineer.container.metadata.MetadataAdapter;

import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.Type;
import java.util.Arrays;

/**
 * @author rees.byars
 */
public class DefaultConstructorResolver implements ConstructorResolver {

    private final MetadataAdapter metadataAdapter;

    public DefaultConstructorResolver(MetadataAdapter metadataAdapter) {
        this.metadataAdapter = metadataAdapter;
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> Constructor<T> resolveConstructor(Class<T> type, Callback callback, Class ... trailingArgs) {
        Type[] genericParameterTypes = {};
        Annotation[][] annotations = {};
        Constructor<T> constructor = null;
        for (Constructor candidateConstructor : type.getDeclaredConstructors()) {
            if (metadataAdapter.isValidConstructor(candidateConstructor)) {
                Type[] candidateTypes = candidateConstructor.getGenericParameterTypes();
                if (candidateTypes.length >= genericParameterTypes.length && candidateTypes.length >= trailingArgs.length) {
                    constructor = candidateConstructor;
                    genericParameterTypes = candidateTypes;
                    annotations = constructor.getParameterAnnotations();
                }
            }
        }
        assert constructor != null;
        constructor.setAccessible(true);
        callback.onResolution(Arrays.copyOf(genericParameterTypes, genericParameterTypes.length - trailingArgs.length), annotations);
        return constructor;
    }

}
