package com.github.overengineer.scope.container.proxy.aop;

import java.lang.reflect.Method;

/**
 */
public class DefaultInterceptorRulesInterpretor implements InterceptorRulesInterpretor {
    @Override
    public boolean appliesToMethod(Interceptor interceptor, Method method) {
        //TODO
        return true;
    }
}
