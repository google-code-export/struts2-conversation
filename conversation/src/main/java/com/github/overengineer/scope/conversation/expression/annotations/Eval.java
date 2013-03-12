package com.github.overengineer.scope.conversation.expression.annotations;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
public @interface Eval {
	
	/**
     * An expression that will be evaluated against the conversations alive on the request using the configured {@link com.com.github.overengineer.scope.conversation.expression.annotations.expression.Eval Eval}
     * prior to the action execution
     * @return
     */
    public abstract String preAction() default "";
    
    /**
     * An expression that will be evaluated against the conversations alive after action execution but before view rendering using the configured {@link com.com.github.overengineer.scope.conversation.expression.annotations.expression.Eval Eval}
     * @return
     */
    public abstract String postAction() default "";
    
    /**
     * An expression that will be evaluated against the conversations alive after action execution and after view rendering using the configured {@link com.com.github.overengineer.scope.conversation.expression.annotations.expression.Eval Eval}
     * @return
     */
    public abstract String postView() default "";

}
