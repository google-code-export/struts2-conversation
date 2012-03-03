package com.google.code.rees.scope.conversation.annotations;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Used to denote an action class is conversation-scoped.
 * <p>
 * When this annotation is used on an action class, then actions within the
 * class default to be members of the conversations specified by this
 * annotation.
 * <p>
 * Depending on the
 * {@link com.google.code.rees.scope.conversation.ConversationArbitrator
 * ConversationArbitrator} being used, by convention any action methods that
 * begin with the word "begin" or "end" are treated as {@link BeginConversation}
 * and {@link EndConversation} methods, respectively.
 * <p>
 * If no conversations are specified, then the name of the conversation is
 * derived from the controller class's name minus the specified controller
 * suffix. The default suffix is "Controller". So the conversation for a class
 * named <code>MyExampleFlowController</code> would be
 * <code>my-example-flow</code>. The action suffix can be set as a property on
 * the ConversationArbitrator.
 * 
 * @author rees.byars
 */
@Target({ java.lang.annotation.ElementType.TYPE })
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface ConversationController {

    public final static String DEFAULT_VALUE = "DEFAULT_VALUE";

    /**
     * Used to specify a single conversation for the controller
     * <p>
     * If no conversations are specified, then the name of the conversation is
     * derived from the controller class's name minus the specified controller
     * suffix. The default suffix is "Controller". So the conversation for a
     * class named <code>MyExampleFlowController</code> would be
     * <code>my-example-flow</code>. The action suffix can be set as a property
     * on the ConversationArbitrator.
     * 
     * 
     */
    public abstract String value() default DEFAULT_VALUE;

    /**
     * Used to specify multiple conversations for the controller
     * <p>
     * If no conversations are specified, then the name of the conversation is
     * derived from the controller class's name minus the specified controller
     * suffix. The default suffix is "Controller". So the conversation for a
     * class named <code>MyExampleFlowController</code> would be
     * <code>my-example-flow</code>. The action suffix can be set as a property
     * on the ConversationArbitrator.
     */
    public abstract String[] conversations() default {};

}
