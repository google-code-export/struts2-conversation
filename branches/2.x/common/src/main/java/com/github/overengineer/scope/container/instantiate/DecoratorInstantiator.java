package com.github.overengineer.scope.container.instantiate;

import com.github.overengineer.scope.container.ComponentStrategy;
import com.github.overengineer.scope.container.Provider;
import com.github.overengineer.scope.container.inject.InjectionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;

/**
 * @author rees.byars
 */
public class DecoratorInstantiator<T> implements Instantiator<T> {

    private static final Logger LOG = LoggerFactory.getLogger(DefaultInstantiator.class);

    private final Class<T> type;
    private Class<?> decoratorDelegateType;
    private ComponentStrategy<?> decoratorDelegateStrategy;
    private transient Constructor<T> constructor;
    private transient ParameterProxy[] parameterProxies;
    private transient Object[] parameters;

    public DecoratorInstantiator(Class<T> type, Class<?> decoratorDelegateType, ComponentStrategy<?> decoratorDelegateStrategy) {
        this.type = type;
        this.decoratorDelegateType = decoratorDelegateType;
        this.decoratorDelegateStrategy = decoratorDelegateStrategy;
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
        boolean decorated = false;
        for (int i = 0; i < parameterTypes.length; i++) {
            Class<?> paramType = parameterTypes[i];
            if (decoratorDelegateType != null && paramType.isAssignableFrom(decoratorDelegateType)) {
                parameterProxies[i] = new DelegateProxy(decoratorDelegateStrategy);
                decorated = true;
            } else {
                parameterProxies[i] = ParameterProxy.Factory.create(parameterTypes[i], annotations[i]);
            }
        }

        //to prevent memory leaks in the case of millions of hot swaps
        if (!decorated) {
            this.decoratorDelegateStrategy = null;
            this.decoratorDelegateType = null;
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
