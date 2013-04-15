package com.github.overengineer.scope.container.proxy.aop;

import java.lang.annotation.*;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface InterceptorRules {

    Class[] classes() default {};
    Class<?> returnType() default PlaceHolder.class;
    Class[] paramterTypes() default PlaceHolder.class;
    Class<? extends Annotation>[] annotations() default PlaceHolder.class;
    String methodNameExpression() default "";
    String classNameExpression() default "";

    public @interface PlaceHolder {}
}
