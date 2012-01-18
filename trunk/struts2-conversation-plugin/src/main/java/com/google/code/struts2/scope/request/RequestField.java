package com.google.code.struts2.scope.request;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Denotes an action field as request scoped.  By default, all Struts2
 * action fields are request scoped.  However, for action classes
 * annotated with {@link ConversationController}, the default becomes
 * conversation scope.  Therefore, this annotation is for use in
 * Conversation Controllers to indicate a request scope.
 */
@Target({java.lang.annotation.ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface RequestField {}
