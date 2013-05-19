package com.github.overengineer.container.metadata;

import com.github.overengineer.container.util.ReflectionUtil;

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
    public Scope getScope(Class cls) {
        if (cls.isAnnotationPresent(Prototype.class)) {
            return NativeScope.PROTOTYPE;
        }
        return NativeScope.SINGLETON;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void initIfEligible(Object component) {
        if (component instanceof PostConstructable) {
            ((PostConstructable) component).init();
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void notifyStartedIfEligible(Object component) {
        throw new UnsupportedOperationException();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void notifyStoppedIfEligible(Object component) {
        throw new UnsupportedOperationException();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isSetter(Method method) {
        return ReflectionUtil.isPublicSetter(method) && (method.isAnnotationPresent(Component.class) || method.isAnnotationPresent(Property.class));
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

    /**
     * {@inheritDoc}
     */
    @Override
    public Method getCustomProviderMethod(Class<?> cls) {
        try {
            for (Method method : cls.getDeclaredMethods()) {
                if (method.getName().equals("get")) {
                    return method;
                }
            }
        } catch (Exception e) {
            throw new MetadataException("Could not obtain provider method named [get] from type [" + cls.getName() + "].  Make sure the class has a method named [get].", e);
        }
        throw new MetadataException("Could not obtain provider method named [get] from type [" + cls.getName() + "].  Make sure the class has a method named [get].", new IllegalArgumentException("There is no method named [get]"));
    }

}
