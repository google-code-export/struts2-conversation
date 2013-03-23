package com.github.overengineer.scope.bijection;

import com.github.overengineer.scope.util.ReflectionUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.lang.reflect.Field;
import java.util.Map;

/**
 *
 */
public class BijectorImpl implements Bijector {

    private static final Logger LOG = LoggerFactory.getLogger(BijectorImpl.class);

    private String fieldName;
    private String contextKey;
    private Class<?> declaringClass;
    private Class<?> fieldType;
    private transient Field field;
    private boolean primitive;

    public BijectorImpl(String contextKey, Field field) {
        fieldName = field.getName();
        this.contextKey = contextKey;
        declaringClass = field.getDeclaringClass();
        this.field = field;
        ReflectionUtil.makeAccessible(field);
        fieldType = field.getType();
        primitive = fieldType.isPrimitive();
    }

    @Override
    public Class<?> getFieldType() {
        return fieldType;
    }

    @Override
    public Class<?> getDeclaringClass() {
         return declaringClass;
    }

    @Override
    public String getContextKey() {
        return contextKey;
    }

    @Override
    public void injectFromContext(Object target, Map<String, Object> context) {
        try {
            Object value = context.get(contextKey);
            if (!(primitive && value == null)) {
                if (LOG.isDebugEnabled()) {
                    LOG.debug("Injecting field [{}] with value [{}] using key [{}] from context [{}]", field.getName(), value, contextKey, context.toString());
                }
                field.set(target, value);
            }
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void extractIntoContext(Object target, Map<String, Object> context) {
        try {
            Object value = field.get(target);
            if (LOG.isDebugEnabled()) {
                LOG.debug("Extracting field [{}] with value [{}] and placing in context [{}] using key [{}]", field.getName(), value, context.toString(), contextKey);
            }
            context.put(contextKey, value);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
        in.defaultReadObject();
        try {
            field = declaringClass.getField(fieldName);
        } catch (NoSuchFieldException e) {
            throw new RuntimeException(e);
        }
    }

}
