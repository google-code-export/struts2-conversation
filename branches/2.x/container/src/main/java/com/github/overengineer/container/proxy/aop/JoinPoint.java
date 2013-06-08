package com.github.overengineer.container.proxy.aop;

import java.lang.reflect.Method;

/**
 * @author rees.byars
 */
public interface JoinPoint<T> {

    T getTarget();

    Object[] getParameters();

    Method getMethod();

    Object join() throws Throwable;

}
