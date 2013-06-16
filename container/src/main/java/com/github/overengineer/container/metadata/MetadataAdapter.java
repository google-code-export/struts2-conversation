package com.github.overengineer.container.metadata;

import com.github.overengineer.container.key.Key;
import com.github.overengineer.container.scope.Scope;
import com.github.overengineer.container.scope.ScopedComponentStrategyProvider;

import java.io.Serializable;
import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.lang.reflect.Type;

/**
 * @author rees.byars
 */
public interface MetadataAdapter extends Serializable {

    MetadataAdapter addScope(Scope scope, Class<? extends Annotation> scopeAnnotation, ScopedComponentStrategyProvider strategyProvider);

    ScopedComponentStrategyProvider getStrategyProvider(Scope scope);

    boolean isValidConstructor(Constructor constructor);

    Scope getScope(Class cls);

    Method getInitMethod(Class<?> cls);

    boolean isSetter(Method method);

    Object getQualifier(Type type, Annotation[] annotations);

    Method getCustomProviderMethod(Class<?> cls);

    Key<?> getDelegateKey(Method method);

    Class<?> getProviderClass();

}
