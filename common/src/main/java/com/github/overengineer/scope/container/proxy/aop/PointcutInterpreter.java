package com.github.overengineer.scope.container.proxy.aop;

import java.lang.reflect.Method;

/**
 */
public interface PointcutInterpreter {
    boolean appliesToMethod(AdvisingInterceptor interceptor, Class targetClass, Method method);
}
