package com.google.code.struts2.scope.conversation;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({java.lang.annotation.ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface ConversationController {
	
	public final static String DEFAULT_VALUE = "DEFAULT_VALUE";
	public abstract String value() default DEFAULT_VALUE;
	public abstract String[] conversations() default {};
	
}
