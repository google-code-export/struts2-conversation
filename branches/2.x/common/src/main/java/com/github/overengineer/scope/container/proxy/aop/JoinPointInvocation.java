package com.github.overengineer.scope.container.proxy.aop;

import java.lang.reflect.Method;

/**
 * @author rees.byars
 */
public interface JoinPointInvocation<T> {
    T getTarget();
    Object[] getParameters();
    Method getMethod();
    Object invoke() throws Exception;
}
