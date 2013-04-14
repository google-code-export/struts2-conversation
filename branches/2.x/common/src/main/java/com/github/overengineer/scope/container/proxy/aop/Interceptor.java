package com.github.overengineer.scope.container.proxy.aop;

/**
 */
public interface Interceptor<T> {

    Object intercept(Invocation<T> invocation) throws Exception;

}
