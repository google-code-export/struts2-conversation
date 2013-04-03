package com.github.overengineer.scope.container;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

/**
 *
 */
public class DefaultInstantiator<T> implements Instantiator<T> {

    private static final Logger LOG = LoggerFactory.getLogger(DefaultInstantiator.class);

    private Class<T> type;
    private transient Constructor<T> constructor;
    private transient ParameterProxy[] parameterProxies;
    private transient Object[] parameters;

    public DefaultInstantiator(Class<T> type) {
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
        constructor.setAccessible(true);
        parameterProxies = new ParameterProxy[parameterTypes.length];
        parameters = new Object[parameterTypes.length];
        for (int i = 0; i < parameterTypes.length; i++) {
            parameterProxies[i] = ParameterProxy.Factory.create(parameterTypes[i], annotations[i]);
        }
    }

    @Override
    public T getInstance(Provider provider) {
        try {
            if (LOG.isDebugEnabled() && parameterProxies.length > 0) {
                LOG.debug("Performing constructor injection on component of type [{}]", type);
            }
            for (int i = 0; i < parameterProxies.length; i++) {
                parameters[i] = parameterProxies[i].get(provider);
            }
            return constructor.newInstance(parameters);
        } catch (Exception e) {
            throw new InjectionException("Could not create new instance of type [" + type.getName() + "]", e);
        }
    }

    private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
        in.defaultReadObject();
        this.init();
    }
}
