package com.google.code.rees.scope.session;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Denotes an action field as session scoped.  Typing is strict, i.e. 
 * impl- and sub-classes cannot be injected into super types and interfaces.
 */
@Target({java.lang.annotation.ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface SessionField {
	
	public static final String DEFAULT = "default";
	
	/**
	 * The name of the field used for matching it in the session.  Fields
	 * with the same name and of the same class are the same session field.
	 */
	public abstract String name() default DEFAULT;
	
}
