package com.github.overengineer.scope.container.standalone;

import com.github.overengineer.scope.container.ScopeContainer;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

/**
 *
 */
public class PrototypeInstantiator<T> implements Instantiator<T> {

    private Class<T> type;
    private transient Constructor<T> constructor;
    private transient ParameterProxy[] parameterProxies;
    private transient Object[] parameters;

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
            parameterProxies[i] = ParameterProxy.Factory.create(parameterTypes[i], annotations[i]);
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
}
