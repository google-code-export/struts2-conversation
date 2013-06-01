package com.github.overengineer.container.metadata;

import java.io.Serializable;
import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.lang.reflect.Type;

/**
 * @author rees.byars
 */
public interface MetadataAdapter extends Serializable {

    boolean isValidConstructor(Constructor constructor);

    Scope getScope(Class cls);

    Method getInitMethod(Class<?> cls);

    void notifyStartedIfEligible(Object component);

    void notifyStoppedIfEligible(Object component);

    boolean isSetter(Method method);

    String getName(Type type, Annotation[] annotations);

    Method getCustomProviderMethod(Class<?> cls);

}
