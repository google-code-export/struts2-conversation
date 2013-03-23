package com.github.overengineer.scope.bijection;

import java.lang.reflect.Field;

/**
 *
 */
public class BijectorImplFactory implements BijectorFactory {
    @Override
    public Bijector create(String contextKey, Field field) {
        return new BijectorImpl(contextKey, field);
    }
}
