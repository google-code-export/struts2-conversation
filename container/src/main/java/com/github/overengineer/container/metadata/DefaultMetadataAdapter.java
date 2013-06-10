package com.github.overengineer.container.metadata;

import com.github.overengineer.container.key.Key;
import com.github.overengineer.container.key.KeyRepository;
import com.github.overengineer.container.key.Qualifier;
import com.github.overengineer.container.scope.Scope;
import com.github.overengineer.container.scope.Scopes;
import com.github.overengineer.container.util.ReflectionUtil;

import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.lang.reflect.Type;

/**
 * @author rees.byars
 */
public class DefaultMetadataAdapter implements MetadataAdapter {

    private final KeyRepository keyRepository;

    public DefaultMetadataAdapter(KeyRepository keyRepository) {
        this.keyRepository = keyRepository;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isValidConstructor(Constructor constructor) {
        return true;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Scope getScope(Class cls) {
        if (cls.isAnnotationPresent(Prototype.class)) {
            return Scopes.PROTOTYPE;
        }
        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Method getInitMethod(Class<?> cls) {
        if (PostConstructable.class.isAssignableFrom(cls)) {
            try {
                return cls.getMethod("init");
            } catch (NoSuchMethodException e) {
                throw new MetadataException("An exception occurred obtaining init method from type [" + cls.getName() + "]", e);
            }
        }
        try {
            for (Method method : cls.getDeclaredMethods()) {
                if (method.getName().equals("postConstruct")) {
                    return method;
                }
            }
        } catch (Exception e) {
            throw new MetadataException("An exception occurred obtaining postConstruct method from type [" + cls.getName() + "]", e);
        }
        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isSetter(Method method) {
        return ReflectionUtil.isPublicSetter(method) && method.isAnnotationPresent(Inject.class);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Object getQualifier(Type type, Annotation[] annotations) {
        for (Annotation annotation : annotations) {
            Class annotationType = annotation.annotationType();
            if (annotationType.isAnnotationPresent(com.github.overengineer.container.metadata.Qualifier.class)) {
                for (Method method : annotationType.getMethods()) {
                    if (method.getName().equals("value")) {
                        try {
                            return method.invoke(annotation);
                        } catch (Exception e) {
                            throw new MetadataException("There was an exception attempting to obtain the value of a qualifier", e);
                        }
                    }
                }
                return annotationType;
            }
        }
        return Qualifier.NONE;
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

    /**
     * {@inheritDoc}
     */
    @Override
    public Key<?> getDelegateKey(Method method) {
        if (!method.isAnnotationPresent(Delegate.class)) {
            throw new MetadataException("There was an exception creating a delegated service", new IllegalArgumentException("The method [" + method.getName() + "] of class [" + method.getDeclaringClass() + "] must be annotated with an @Delegate annotation"));
        }
        Delegate delegate = method.getAnnotation(Delegate.class);
        String name = delegate.name();
        if ("".equals(name)) {
            return keyRepository.retrieveKey(delegate.value(), Qualifier.NONE);
        }
        return keyRepository.retrieveKey(delegate.value(), name);
    }

}
