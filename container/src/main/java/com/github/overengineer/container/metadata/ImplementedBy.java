package com.github.overengineer.container.metadata;

import java.lang.annotation.*;

/**
 * @author rees.byars
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface ImplementedBy {
    Class<?> value();
    String name() default "";
}