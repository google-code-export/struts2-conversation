package com.github.overengineer.scope.container.proxy.aop;

/**
 */
public interface AdvisingInterceptor<T> {

    Object intercept(JoinPointInvocation<T> invocation) throws Throwable;

}
