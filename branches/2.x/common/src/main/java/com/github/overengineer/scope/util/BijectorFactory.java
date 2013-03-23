package com.github.overengineer.scope.util;

import java.io.Serializable;
import java.lang.reflect.Field;

/**
 *
 */
public interface BijectorFactory extends Serializable {
    Bijector create(String contextKey, Field field);
}
