package com.github.overengineer.container.proxy;

import java.io.Serializable;

/**
 * @author rees.byars
 */
public interface ProxyHandlerFactory extends Serializable {

    <T> ComponentProxyHandler<T> createProxy(Class<?> targetClass);

}
