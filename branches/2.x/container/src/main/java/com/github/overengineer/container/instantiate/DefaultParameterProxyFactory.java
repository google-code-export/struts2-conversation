package com.github.overengineer.container.instantiate;

import com.github.overengineer.container.key.KeyRepository;
import com.github.overengineer.container.key.KeyUtil;
import com.github.overengineer.container.metadata.MetadataAdapter;

import java.lang.annotation.Annotation;
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
}
