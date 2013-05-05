package com.github.overengineer.scope.container.instantiate;

import com.github.overengineer.scope.container.Provider;
import com.github.overengineer.scope.container.inject.InjectionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

/**
 * @author rees.byars
 */
public class DefaultInstantiator<T> implements Instantiator<T> {

    private static final Logger LOG = LoggerFactory.getLogger(DefaultInstantiator.class);

    private final Class<T> type;
    private transient Constructor<T> constructor;
    private transient ParameterProxy[] parameterProxies;
    private transient Object[] parameters;

    public DefaultInstantiator(Class<T> type) {
        this.type = type;
        this.init();
    }

    @SuppressWarnings("unchecked")
    private void init() {
        Type[] genericParameterTypes = {};
        Annotation[][] annotations = {};
        for (Constructor candidateConstructor : type.getDeclaredConstructors()) {
            Type[] candidateTypes = candidateConstructor.getGenericParameterTypes();
            if (candidateTypes.length >= genericParameterTypes.length) {
                constructor = candidateConstructor;
                genericParameterTypes = candidateTypes;
                annotations = constructor.getParameterAnnotations();
            }
        }
        constructor.setAccessible(true);
        parameterProxies = new ParameterProxy[genericParameterTypes.length];
        parameters = new Object[genericParameterTypes.length];
        for (int i = 0; i < genericParameterTypes.length; i++) {
            parameterProxies[i] = ParameterProxy.Factory.create(genericParameterTypes[i], annotations[i]);
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
