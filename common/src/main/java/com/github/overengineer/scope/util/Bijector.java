package com.github.overengineer.scope.util;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.util.Map;

/**
 *
 */
public interface Bijector extends Serializable {

    void injectFromContext(Object target, Map<String, Object> context);

    void extractIntoContext(Object target, Map<String, Object> context);

    interface Factory extends Serializable {
        Bijector create(String contextKey, Field field);
    }

}
