package com.github.overengineer.scope.util;

import java.io.Serializable;
import java.util.Map;

/**
 *
 */
public interface Bijector extends Serializable {

    Class<?> getFieldType();

    Class<?> getDeclaringClass();

    String getContextKey();

    void injectFromContext(Object target, Map<String, Object> context);

    void extractIntoContext(Object target, Map<String, Object> context);

}
