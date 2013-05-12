package com.github.overengineer.container.metadata;

import com.github.overengineer.container.Scope;

import java.io.Serializable;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.Type;

/**
 * @author rees.byars
 */
public interface MetadataAdapter extends Serializable {

    Scope getScope(Class cls);

    void initIfEligible(Object component);

    void notifyStartedIfEligible(Object component);

    void notifyStoppedIfEligible(Object component);

    boolean isComponentSetter(Method method);

    String getPropertyName(Method method);

    String getPropertyName(Type type, Annotation[] annotations);

}
