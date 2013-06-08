package com.github.overengineer.container.dynamic;

import com.github.overengineer.container.Provider;
import com.github.overengineer.container.inject.MethodInjector;
import com.github.overengineer.container.key.Key;

import java.io.Serializable;

/**
 * @author rees.byars
 */
public class ServiceDelegateInvoker<T> implements Serializable {

    private final Key<T> serviceDelegateKey;
    private final MethodInjector<T> methodInjector;
    private final Provider provider;

    ServiceDelegateInvoker(Key<T> serviceDelegateKey, MethodInjector<T> methodInjector, Provider provider) {
        this.serviceDelegateKey = serviceDelegateKey;
        this.methodInjector = methodInjector;
        this.provider = provider;
    }

    public Object invoke(Object[] trailingsArgs) {
        return methodInjector.inject(provider.get(serviceDelegateKey), provider, trailingsArgs);
    }
}
