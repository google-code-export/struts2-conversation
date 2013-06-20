package com.github.overengineer.container.metadata;

import com.github.overengineer.container.key.Key;
import com.github.overengineer.container.key.Locksmith;
import com.github.overengineer.container.key.Qualifier;
import com.github.overengineer.container.scope.Scope;
import com.github.overengineer.container.scope.ScopedComponentStrategyProvider;
import com.github.overengineer.container.scope.Scopes;
import com.github.overengineer.container.util.ReflectionUtil;

import javax.inject.Singleton;
import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

/**
 * @author rees.byars
 */
public class Jsr330MetadataAdapter implements MetadataAdapter {

    private final Map<Class<? extends Annotation>, Scope> scopes = new HashMap<Class<? extends Annotation>, Scope>();
    private final Map<Scope, ScopedComponentStrategyProvider> strategyProviders = new HashMap<Scope, ScopedComponentStrategyProvider>();

    {
        scopes.put(Singleton.class, Scopes.SINGLETON);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public MetadataAdapter addScope(Scope scope, Class<? extends Annotation> scopeAnnotation, ScopedComponentStrategyProvider strategyProvider) {
        scopes.put(scopeAnnotation, scope);
        strategyProviders.put(scope, strategyProvider);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ScopedComponentStrategyProvider getStrategyProvider(Scope scope) {
        return strategyProviders.get(scope);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isValidConstructor(Constructor constructor) {

        if (constructor.getParameterTypes().length == 0) {
            return true;
        }

        //TODO only allow on a single constructor - up to the resolver?? :/
        for (Annotation annotation : constructor.getAnnotations()) {
            if (annotation instanceof javax.inject.Inject) {
                return true;
            }
        }
        return false;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Scope getScope(Class cls) {
        for (Annotation annotation : cls.getAnnotations()) {
            Class annotationType = annotation.annotationType();
            if (annotationType.isAnnotationPresent(javax.inject.Scope.class)) {
                for (Method method : annotationType.getMethods()) {
                    if (method.getName().equals("value") && Scope.class.isAssignableFrom(method.getReturnType())) {
                        try {
                            return (Scope) method.invoke(annotation);
                        } catch (Exception e) {
                            throw new MetadataException("There was an exception attempting to obtain the value of a qualifier", e);
                        }
                    }
                }
                return scopes.get(annotationType);
            }
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
        return method.isAnnotationPresent(javax.inject.Inject.class);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Object getQualifier(Type type, Annotation[] annotations) {
        for (Annotation annotation : annotations) {
            Class annotationType = annotation.annotationType();
            if (annotationType.isAnnotationPresent(javax.inject.Qualifier.class)) {
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
        if (!method.isAnnotationPresent(ImplementedBy.class)) {
            throw new MetadataException("There was an exception creating a delegated service", new IllegalArgumentException("The method [" + method.getName() + "] of class [" + method.getDeclaringClass() + "] must be annotated with an @Delegate annotation"));
        }
        ImplementedBy delegate = method.getAnnotation(ImplementedBy.class);
        String name = delegate.name();
        if ("".equals(name)) {
            return Locksmith.makeKey(delegate.value(), Qualifier.NONE);
        }
        return Locksmith.makeKey(delegate.value(), name);
    }

    @Override
    public Class<?> getProviderClass() {
        return javax.inject.Provider.class;
    }

}
