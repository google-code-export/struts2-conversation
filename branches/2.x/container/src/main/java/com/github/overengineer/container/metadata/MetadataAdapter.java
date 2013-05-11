package com.github.overengineer.container.metadata;

import java.io.Serializable;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.Type;

/**
 * @author rees.byars
 */
public interface MetadataAdapter extends Serializable {

    boolean isComponentSetter(Method method);

    String getPropertyName(Method method);

    String getPropertyName(Type type, Annotation[] annotations);

}
