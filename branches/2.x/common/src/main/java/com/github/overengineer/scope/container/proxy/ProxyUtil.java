package com.github.overengineer.scope.container.proxy;

import java.lang.reflect.Proxy;

/**
 */
public class ProxyUtil {

    @SuppressWarnings("unchecked")
    public static <T> T getRealComponent(T proxy) {
        if (Proxy.isProxyClass(proxy.getClass())) {
            return ((ComponentProxyHandler<T>) Proxy.getInvocationHandler(proxy)).getComponent();
        }
        return proxy;
    }

}
