package com.google.code.rees.scope.spring;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.springframework.stereotype.Controller;

@Target({ java.lang.annotation.ElementType.TYPE })
@Retention(RetentionPolicy.RUNTIME)
@Controller
@Documented
public @interface ConversationController {

    public final static String DEFAULT_VALUE = "";

    public abstract String value() default DEFAULT_VALUE;

    public abstract String[] conversations() default {};
}
