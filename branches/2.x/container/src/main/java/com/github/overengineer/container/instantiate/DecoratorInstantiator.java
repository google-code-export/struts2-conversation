package com.github.overengineer.container.instantiate;

import com.github.overengineer.container.ComponentStrategy;
import com.github.overengineer.container.Provider;
import com.github.overengineer.container.inject.InjectionException;
import com.github.overengineer.container.key.KeyUtil;
import com.github.overengineer.container.key.TempKey;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.Type;

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
        boolean decorated = false;
        for (int i = 0; i < genericParameterTypes.length; i++) {
            Type paramType = genericParameterTypes[i];
            //TODO make work for generics!!! need to compare keys
            if (decoratorDelegateType != null && KeyUtil.getClass(paramType).isAssignableFrom(decoratorDelegateType)) {
                parameterProxies[i] = new DelegateProxy(decoratorDelegateStrategy);
                decorated = true;
            } else {
                parameterProxies[i] = ParameterProxy.Factory.create(paramType, annotations[i]);
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
