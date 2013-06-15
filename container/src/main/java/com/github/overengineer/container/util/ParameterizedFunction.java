package com.github.overengineer.container.util;

import java.io.Serializable;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

/**
 * @author rees.byars
 */
public interface ParameterizedFunction extends Serializable {
    Type[] getParameterTypes();
    Annotation[][] getParameterAnnotations();
}
