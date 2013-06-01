package com.github.overengineer.container.parameter;

import com.github.overengineer.container.key.KeyRepository;
import com.github.overengineer.container.metadata.MetadataAdapter;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.Type;

/**
 * @author rees.byars
 */
public class DefaultParameterProxyFactory implements ParameterProxyFactory {

    private final MetadataAdapter metadataAdapter;
    private final KeyRepository keyRepository;

    public DefaultParameterProxyFactory(MetadataAdapter metadataAdapter, KeyRepository keyRepository) {
        this.metadataAdapter = metadataAdapter;
        this.keyRepository = keyRepository;
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> ParameterProxy<T> create(Type type, Annotation[] annotations) {

        return new ComponentParameterProxy<T>(keyRepository.retrieveKey(type, metadataAdapter.getName(type, annotations)));

    }

    @SuppressWarnings("unchecked")
    @Override
    public ParameterProxy[] create(Method method) {

        Type[] genericParameterTypes = method.getGenericParameterTypes();
        Annotation[][] annotations = method.getParameterAnnotations();

        ParameterProxy[] parameterProxies = new ParameterProxy[genericParameterTypes.length];
        for (int i = 0; i < genericParameterTypes.length; i++) {
            parameterProxies[i] = create(genericParameterTypes[i], annotations[i]);
        }

        return parameterProxies;
    }

}
