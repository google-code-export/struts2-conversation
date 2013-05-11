package com.github.overengineer.container.metadata;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.Type;

/**
 * @author rees.byars
 */
public interface MetadataAdapter {

    boolean isComponentSetter(Method method);

    String getPropertyName(Method method);

    String getPropertyName(Type type, Annotation[] annotations);

}
