package com.github.overengineer.container.dynamic;

import com.github.overengineer.container.Provider;
import com.github.overengineer.container.inject.InjectorFactory;
import com.github.overengineer.container.inject.MethodInjector;
import com.github.overengineer.container.key.Key;
import com.github.overengineer.container.metadata.MetadataAdapter;
import com.github.overengineer.container.parameter.ParameterMatchingUtil;

import java.io.Serializable;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

/**
 * @author rees.byars
 */
public class DelegatingService<T> implements InvocationHandler, Serializable {

    private final Class<T> serviceInterface;
    private transient Map<Method, ServiceDelegateInvoker> delegateInvokerCache;
    private final InjectorFactory injectorFactory;
    private final MetadataAdapter metadataAdapter;
    private final Provider provider;
    T proxy;

    DelegatingService(Class<T> serviceInterface, Provider provider, InjectorFactory injectorFactory, MetadataAdapter metadataAdapter) {
        this.serviceInterface = serviceInterface;
        this.provider = provider;
        this.injectorFactory = injectorFactory;
        this.metadataAdapter = metadataAdapter;
        initCache();
    }

    @Override
    public Object invoke(Object o, Method method, Object[] objects) throws Throwable {
        String methodName = method.getName();
        if ("equals".equals(methodName)) {
            return proxy == objects[0];
        } else if ("hashCode".equals(methodName)) {
            return System.identityHashCode(proxy);
        } else if ("toString".equals(methodName)) {
            return proxy.getClass().getName() + '@' + Integer.toHexString(System.identityHashCode(this)) + "$DelegatingService$[" + serviceInterface.getName() + "]";
        }
        if (delegateInvokerCache == null) {
            synchronized (this) {
                if (delegateInvokerCache == null) {
                    initCache();
                }
            }
        }
        return delegateInvokerCache.get(method).invoke(objects);
    }

    private void initCache() {
        delegateInvokerCache = new HashMap<Method, ServiceDelegateInvoker>();
        for (Method serviceMethod : serviceInterface.getDeclaredMethods()) {
            Key delegateKey = metadataAdapter.getDelegateKey(serviceMethod);
            Class<?> delegateClass = delegateKey.getTargetClass();
            Class[] providedArgs = serviceMethod.getParameterTypes();
            Method delegateMethod = null;
            for (Method delegateCandidateMethod : delegateClass.getDeclaredMethods()) {
                //TODO make the provided arg matching injectable to match different parameter builders
                if (delegateCandidateMethod.getName().equals(serviceMethod.getName()) && ParameterMatchingUtil.precedingMatch(providedArgs, delegateCandidateMethod.getParameterTypes())) {
                    delegateMethod = delegateCandidateMethod;
                    break;
                }
            }
            if (delegateMethod == null) {
                //TODO mrpvoe error message
                throw new IllegalArgumentException("No valid delegate methods could be found");
            }
            MethodInjector delegateMethodInjector = injectorFactory.create(delegateClass, delegateMethod, providedArgs);
            @SuppressWarnings("unchecked")
            ServiceDelegateInvoker<T> serviceDelegateInvoker = new ServiceDelegateInvoker<T>(delegateKey, delegateMethodInjector, provider);
            delegateInvokerCache.put(serviceMethod, serviceDelegateInvoker);
        }
    }

}
