package com.github.overengineer.container.instantiate;

import com.github.overengineer.container.Provider;
import com.github.overengineer.container.inject.InjectionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.lang.reflect.Constructor;

/**
 * @author rees.byars
 */
public class DefaultInstantiator<T> implements Instantiator<T> {

    private static final Logger LOG = LoggerFactory.getLogger(DefaultInstantiator.class);

    private final Class<T> type;
    private final ParameterProxyProvider parameterProxyProvider;
    private transient Constructor<T> constructor;
    private transient Object[] parameters;

    public DefaultInstantiator(Class<T> type, ParameterProxyProvider parameterProxyProvider) {
        this.type = type;
        this.parameterProxyProvider = parameterProxyProvider;
        constructor = new DefaultConstructorResolver().resolveConstructor(type, parameterProxyProvider);
        parameters = new Object[parameterProxyProvider.getParameterProxies().length];
    }

    @Override
    public T getInstance(Provider provider) {
        ParameterProxy[] parameterProxies = parameterProxyProvider.getParameterProxies();
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
        constructor = new DefaultConstructorResolver().resolveConstructor(type, parameterProxyProvider);
        parameters = new Object[parameterProxyProvider.getParameterProxies().length];
    }
}
