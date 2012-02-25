package com.google.code.rees.scope.conversation.annotations;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Denotes a method as a conversation-initiating method. Depending on
 * the {@link com.google.code.rees.scope.conversation.ConversationArbitrator
 * ConversationArbitrator} being used, the convention of
 * using the {@link ConversationController} annotation and beginning the name of
 * an action method with "begin" can be used instead of this annotation.
 * 
 * @author rees.byars
 * 
 * @see #conversations()
 * 
 */
@Target({ java.lang.annotation.ElementType.METHOD })
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface BeginConversation {

    /**
     * The conversations for which this method will act as an begin-point.
     * <p>
     * In an action class annotated with the {@link ConversationController}
     * annotation, the {@link #conversations()} field will default to that of
     * the controller's conversations if left blank. If not in a
     * ConversationController, then the conversations field must be specified in
     * order to associate this begin point.
     */
    public abstract String[] conversations() default {};

}
