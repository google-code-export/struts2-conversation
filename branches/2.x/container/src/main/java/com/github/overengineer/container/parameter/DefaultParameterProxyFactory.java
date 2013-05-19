package com.github.overengineer.container.parameter;

import com.github.overengineer.container.key.KeyRepository;
import com.github.overengineer.container.key.KeyUtil;
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

        String propertyName = metadataAdapter.getPropertyName(type, annotations);

        if (propertyName == null) {
            return new ComponentParameterProxy<T>(keyRepository.retrieveKey(type));
        }

        return new PropertyParameterProxy<T>(KeyUtil.getClass(type), propertyName);

    }

    @SuppressWarnings("unchecked")
    @Override
    public ParameterProxy[] create(Method method) {

        String propertyName = metadataAdapter.getPropertyName(method);

        if (propertyName != null) {
            return new ParameterProxy[]{new PropertyParameterProxy(method.getParameterTypes()[0], propertyName)};
        }

        Type[] genericParameterTypes = method.getGenericParameterTypes();
        Annotation[][] annotations = method.getParameterAnnotations();

        ParameterProxy[] parameterProxies = new ParameterProxy[genericParameterTypes.length];
        for (int i = 0; i < genericParameterTypes.length; i++) {
            parameterProxies[i] = create(genericParameterTypes[i], annotations[i]);
        }

        return parameterProxies;
    }

}
