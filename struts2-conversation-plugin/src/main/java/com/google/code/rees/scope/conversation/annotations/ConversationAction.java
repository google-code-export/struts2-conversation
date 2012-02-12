package com.google.code.rees.scope.conversation.annotations;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Denotes an action as an intermediate member a conversation.
 * <p>
 * Only works on methods that are also actions. Depending on the
 * {@link com.google.code.rees.scope.conversation.ConversationArbitrator
 * ConversationArbitrator} being used, the convention of using the
 * {@link ConversationController} can replace the need for this annotation in
 * most cases.
 * <P>
 * 
 * @author rees.byars
 * 
 * @see #conversations()
 */
@Target({ java.lang.annotation.ElementType.METHOD })
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface ConversationAction {

    /**
     * The conversations for which this method is a member.
     * <p>
     * In an action class annotated with the {@link ConversationController}
     * annotation, the {@link #conversations()} field will default to that of
     * the controller's conversations if left blank. If not in a
     * ConversationController, then the conversations field must be specified in
     * order to associate this action.
     */
    public abstract String[] conversations() default {};

}
