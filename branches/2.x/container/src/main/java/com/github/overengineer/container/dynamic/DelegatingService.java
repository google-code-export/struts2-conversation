package com.github.overengineer.container.dynamic;

import com.github.overengineer.container.util.MethodCacheKey;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.Map;

/**
 * @author rees.byars
 */
public class DelegatingService<T> implements InvocationHandler {

    private final Class<T> serviceInterface;
    private final Map<MethodCacheKey, ServiceDelegateInvoker> delegateInvokerCache;
    T proxy;

    DelegatingService(Class<T> serviceInterface, Map<MethodCacheKey, ServiceDelegateInvoker> delegateInvokerCache) {
        this.serviceInterface = serviceInterface;
        this.delegateInvokerCache = delegateInvokerCache;
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
        return delegateInvokerCache.get(new MethodCacheKey(method)).invoke(objects);
    }

}
