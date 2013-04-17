package com.github.overengineer.scope.container.proxy.aop;

/**
 */
public interface Aspect<T> {

    Object advise(JoinPointInvocation<T> invocation) throws Throwable;

}
