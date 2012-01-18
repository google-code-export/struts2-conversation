package com.google.code.rees.scope.conversation;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Denotes an action as an intermediate member a conversation.
 * <p>
 * Only works on methods that are also actions.  The method may
 * be specified as an action through configuration in the struts.xml,
 * by convention by being named <code>execute()</code> in an action class,
 * of by annotation with the convention plug-in's Action annotation.
 * <P>
 * @see #conversations()
 */
@Target({java.lang.annotation.ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface ConversationAction {
	
	/**
	 * The conversations for which this method is a member.
	 * <p>
	 * In an action class annotated with the {@link ConversationController} annotation,
	 * the {@link #conversations()} field will default to that of the controller's 
	 * conversations if left blank.  If not in a ConversationController, then
	 * the conversations field must be specified in order to associate this action.
	 */
	public abstract String[] conversations() default {};
	
}
