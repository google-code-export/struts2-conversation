package com.github.overengineer.scope.container.proxy.aop;

import java.lang.reflect.Method;

/**
 */
public interface InterceptorRulesInterpretor {
    boolean appliesToMethod(Interceptor interceptor, Class targetClass, Method method);
}
