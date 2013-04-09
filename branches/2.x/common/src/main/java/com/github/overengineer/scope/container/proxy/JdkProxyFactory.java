package com.github.overengineer.scope.container.proxy;

import java.io.Serializable;
import java.lang.reflect.InvocationHandler;

/**
 */
public interface JdkProxyFactory extends Serializable {
    Object newProxyInstance(InvocationHandler invocationHandler);
}
