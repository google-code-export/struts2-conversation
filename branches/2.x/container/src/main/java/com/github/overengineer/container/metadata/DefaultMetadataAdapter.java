package com.github.overengineer.container.metadata;

import com.github.overengineer.container.Component;
import com.github.overengineer.container.Property;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.Type;

/**
 * @author rees.byars
 */
public class DefaultMetadataAdapter implements MetadataAdapter {

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isComponentSetter(Method method) {
        return method.isAnnotationPresent(Component.class);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getPropertyName(Method method) {
        if (method.isAnnotationPresent(Property.class)) {
            return method.getAnnotation(Property.class).value();
        }
        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getPropertyName(Type type, Annotation[] annotations) {
        for (Annotation annotation : annotations) {
            if (annotation instanceof Property) {
                return ((Property) annotation).value();
            }
        }
        return null;
    }

}
