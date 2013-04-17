package com.github.overengineer.scope.container.proxy.aop;

import java.lang.reflect.Method;

/**
 */
public interface PointcutInterpreter {
    boolean appliesToMethod(Aspect aspect, Class targetClass, Method method);
}
