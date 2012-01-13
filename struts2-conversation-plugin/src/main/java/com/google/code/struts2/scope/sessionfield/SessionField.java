package com.google.code.struts2.scope.sessionfield;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({java.lang.annotation.ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface SessionField {
	
	public static final String DEFAULT = "default";
	
	public abstract String name() default DEFAULT;
	
}
