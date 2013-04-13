package com.github.overengineer.scope.container.proxy;

import java.lang.reflect.Method;

/**
 * @author rees.byars
 */
public interface Invocation<T> {
    T getTarget();
    Object[] getParameters();
    Method getMethod();
    Object invoke() throws Exception;
}
