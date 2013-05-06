package com.github.overengineer.container.proxy.aop;

import java.lang.annotation.*;

/**
 * @author rees.byars
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Pointcut {

    Class[] classes() default {};

    Class<?> returnType() default PlaceHolder.class;

    Class[] paramterTypes() default PlaceHolder.class;

    Class<? extends Annotation>[] annotations() default {};

    String methodNameExpression() default "";

    String classNameExpression() default "";

    public @interface PlaceHolder {}
}
