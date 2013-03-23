package com.github.overengineer.scope.util;

import java.lang.reflect.Field;
import java.util.Map;

/**
 *
 */
public class BijectorImpl implements Bijector {

    private String fieldName;
    private String contextKey;
    private Class<?> declaringClass;
    private transient Field field;

    public BijectorImpl(String contextKey, Field field) {
        fieldName = field.getName();
        this.contextKey = contextKey;
        declaringClass = field.getDeclaringClass();
        this.field = field;
    }

    @Override
    public void injectFromContext(Object target, Map<String, Object> context) {
        try {
            if (field == null) {
                field = declaringClass.getField(fieldName);
            }
            Object value = context.get(contextKey);
            field.set(target, value);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        } catch (NoSuchFieldException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void extractIntoContext(Object target, Map<String, Object> context) {
        try {
            if (field == null) {
                field = declaringClass.getField(fieldName);
            }
            Object value = field.get(target);
            context.put(contextKey, value);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        } catch (NoSuchFieldException e) {
            throw new RuntimeException(e);
        }
    }

    public static class Factory implements Bijector.Factory {
        @Override
        public Bijector create(String contextKey, Field field) {
            return new BijectorImpl(contextKey, field);
        }
    }

}
