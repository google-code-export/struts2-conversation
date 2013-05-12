package com.github.overengineer.container.metadata;

import com.github.overengineer.container.Component;
import com.github.overengineer.container.Property;
import com.github.overengineer.container.Scope;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.Type;

/**
 * @author rees.byars
 */
public class DefaultMetadataAdapter implements MetadataAdapter {

    @Override
    public Scope getScope(Class cls) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void initIfEligible(Object component) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void notifyStartedIfEligible(Object component) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void notifyStoppedIfEligible(Object component) {
        throw new UnsupportedOperationException();
    }

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
