package com.google.code.rees.scope.conversation.annotations;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Denotes a field as a conversation-scoped field.
 * 
 * @see #name()
 * @see #conversations()
 *
 */
@Target({java.lang.annotation.ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface ConversationField {
	
	public static final String DEFAULT = "default";
	
	/**
	 * The name of the field.  If left blank, then the name of the
	 * field defaults to the name by which it is called in the code.
	 * For instance, for:
	 * <p>
	 *  <code>@ConversationField String someString;</code>
	 *  <p>
	 *  the name would default to "someString".  As well,
	 *  the following will be treated as the same conversation field:
	 *  <p>
	 *  <code>@ConversationField String someString;</code><br>
	 *  <code>@ConversationField(name="someString") String dumbString;</code>
	 *  <p>
	 *  because they have the same <i>conversation field name<i>.
	 */
	public abstract String name() default DEFAULT;
	
	/**
	 * The conversations for which this field will be a member.
	 * <p>
	 * In an action class annotated with the {@link ConversationController} annotation,
	 * the {@link #conversations()} field will default to that of the controller's 
	 * conversations if left blank.  If not in a ConversationController, then
	 * the conversations field must be specified in order to associate this field.
	 */
	public abstract String[] conversations() default {};
	
}
