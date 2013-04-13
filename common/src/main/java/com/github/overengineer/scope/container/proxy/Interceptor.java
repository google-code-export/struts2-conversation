package com.github.overengineer.scope.container.proxy;

/**
 */
public interface Interceptor<T> {

    Object intercept(Invocation<T> invocation) throws Exception;

}
