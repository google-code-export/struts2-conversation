package com.github.overengineer.scope.container.proxy.aop;

import java.lang.annotation.*;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface InterceptorRules {
    Class[] classes();
    Class<?> returnType();
    Class[] paramterTypes();
    String methodNameExpression();
    String classNameExpression();
}
