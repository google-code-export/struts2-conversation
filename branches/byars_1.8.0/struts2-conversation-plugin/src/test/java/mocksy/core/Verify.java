package mocksy.core;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * User: reesbyars
 * Date: 9/17/12
 * Time: 12:53 PM
 * <p/>
 * Verify
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Verify {
    int calls() default -1;
    int minCalls() default -1;
    int maxCalls() default -1;
}
