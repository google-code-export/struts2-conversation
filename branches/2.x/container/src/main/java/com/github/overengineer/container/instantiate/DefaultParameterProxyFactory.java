package com.github.overengineer.container.instantiate;

import com.github.overengineer.container.key.KeyUtil;
import com.github.overengineer.container.metadata.MetadataAdapter;

import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

/**
 * @author rees.byars
 */
public class DefaultParameterProxyFactory implements ParameterProxyFactory {

    private final MetadataAdapter metadataAdapter;

    public DefaultParameterProxyFactory(MetadataAdapter metadataAdapter) {
        this.metadataAdapter = metadataAdapter;
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> ParameterProxy<T> create(Type type, Annotation[] annotations) {

        String propertyName = metadataAdapter.getPropertyName(type, annotations);

        if (propertyName == null) {
            return new ComponentParameterProxy<T>(type);
        }

        return new PropertyParameterProxy<T>(KeyUtil.getClass(type), propertyName);

    }
}
