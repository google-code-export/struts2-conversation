package com.github.overengineer.scope.container.standalone;

import com.github.overengineer.scope.container.Property;
import com.github.overengineer.scope.container.ScopeContainer;
import com.github.overengineer.scope.container.standalone.Instantiator;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

/**
 *
 */
public class PrototypeInstantiator<T> implements Instantiator<T> {

    private Class<T> type;
    private transient Constructor<T> constructor;
    private ParameterProxy[] parameterProxies;
    private Object[] parameters;

    public PrototypeInstantiator(Class<T> type) {
        this.type = type;
        this.init();
    }

    @SuppressWarnings("unchecked")
    private void init() {
        Class[] parameterTypes = {};
        Annotation[][] annotations = {};
        for (Constructor candidateConstructor : type.getDeclaredConstructors()) {
            Class[] candidateTypes = candidateConstructor.getParameterTypes();
            if (candidateTypes.length >= parameterTypes.length) {
                constructor = candidateConstructor;
                parameterTypes = candidateTypes;
                annotations = constructor.getParameterAnnotations();
            }
        }
        if (constructor == null) {
            return;
        }
        constructor.setAccessible(true);
        parameterProxies = new ParameterProxy[parameterTypes.length];
        parameters = new Object[parameterTypes.length];
        for (int i = 0; i < parameterTypes.length; i++) {
            parameterProxies[i] = new ParameterProxy(parameterTypes[i], annotations[i]);
        }
    }

    @Override
    public Class<T> getTargetType() {
        return type;
    }

    @Override
    public T getInstance(ScopeContainer container) {
        try {
            if (constructor == null) {
                return type.newInstance();
            }
            for (int i = 0; i < parameterProxies.length; i++) {
                parameters[i] = parameterProxies[i].get(container);
            }
            return constructor.newInstance(parameters);
        } catch (InstantiationException e) {
            throw new RuntimeException(e);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        } catch (InvocationTargetException e) {
            throw new RuntimeException(e);
        }
    }

    private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
        in.defaultReadObject();
        this.init();
    }

    private static class ParameterProxy implements Serializable {
        Class<?> parameterType;
        boolean property = false;
        String name;
        ParameterProxy(Class<?> parameterType, Annotation[] annotations) {
            this.parameterType = parameterType;
            for (Annotation annotation : annotations) {
                if (annotation instanceof Property) {
                   property = true;
                    name = ((Property) annotation).value();
                }
            }
        }
        public Object get(ScopeContainer container) {
            if (property) {
                return container.getProperty(parameterType, name);
            }
            return container.getComponent(parameterType);
        }
    }
}
