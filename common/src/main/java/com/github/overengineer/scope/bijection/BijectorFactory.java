package com.github.overengineer.scope.bijection;

import java.io.Serializable;
import java.lang.reflect.Field;

/**
 *
 */
public interface BijectorFactory extends Serializable {
    Bijector create(String contextKey, Field field);
}
