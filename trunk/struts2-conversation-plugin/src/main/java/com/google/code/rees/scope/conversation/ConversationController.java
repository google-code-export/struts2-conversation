package com.google.code.rees.scope.conversation;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Used to denote an action class is conversation-scoped.
 * <p>
 * When this annotation is used on an action class, then fields and
 * actions within the class default to be members of the conversations
 * specified by this annotation.
 * <p>
 * In order for a field not to become a member of the conversations,
 * the fields must be annotated either with {@link com.google.code.rees.scope.request.RequestField 
 * RequestField} or {@link com.google.code.rees.scope.sessionfield.SessionField 
 * SessionField} or Spring's @Autowired.  As well, <code>static</code> 
 * and <code>final</code> fields are ignored.
 * <p>
 * Additional conversation-negating annotations may be specified as a comma-separated list
 * in the struts.xml such as:
 * <p><code>
 * &ltconstant name="struts.conversation.overridingAnnotations" <br>
 * 	&nbsp;&nbsp;value="org.springframework.beans.factory.annotation.Autowired, <br>
 *  &nbsp;&nbsp;org.your.CustomAnnotation"/>
 * </code>
 * <p>
 * As well, by convention any action methods that begin with the word "begin" or "end"
 * are treated as {@link BeginConversation} and {@link EndConversation} methods, respectively.
 * This behavior is turned of by setting the following in your struts.xml:
 * <p><code>
 * &ltconstant name="struts.scope.followsConvention" value="true" />
 * </code>
 * <p>
 * If no conversations are specified, then the name of the 
 * conversation is derived from the controller class's name
 * using the following the same convention applied to
 * action names by the convention controller, for example:
 * <p>
 * If the Action suffix is specified in the struts.xml as:<br><code>
 * &ltconstant name="struts.convention.action.suffix" value="Controller"/>
 * </code><p>
 * Then <code>CheckoutFlowController</code> would be associated with
 * a conversation named <code>checkout-flow</code>.
 * <p>
 * Note:  by default, the action name suffix is set to "Action", not "Controller".
 */
@Target({java.lang.annotation.ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface ConversationController {
	
	public final static String DEFAULT_VALUE = "DEFAULT_VALUE";
	
	/**
	 * Used to specify a single conversation for the controller
	 * <p>
	 * If no conversations are specified, then the name of the 
	 * conversation is derived from the controller class's name
	 * using the following the same convention applied to
	 * action names by the convention controller, for example:
	 * <p>
	 * If the Action suffix is specified in the struts.xml as:<br><code>
	 * &ltconstant name="struts.convention.action.suffix" value="Controller"/>
	 * </code><p>
	 * Then <code>CheckoutFlowController</code> would be associated with
	 * a conversation named <code>checkout-flow</code>.
	 * 
	 * 
	 */
	public abstract String value() default DEFAULT_VALUE;
	
	/**
	 * Used to specify multiple conversations for the controller
	 * <p>
	 * If no conversations are specified, then the name of the 
	 * conversation is derived from the controller class's name
	 * using the following the same convention applied to
	 * action names by the convention controller, for example:
	 * <p>
	 * If the Action suffix is specified in the struts.xml as:<br><code>
	 * &ltconstant name="struts.convention.action.suffix" value="Controller"/>
	 * </code><p>
	 * Then <code>CheckoutFlowController</code> would be associated with
	 * a conversation named <code>checkout-flow</code>.
	 */
	public abstract String[] conversations() default {};
	
}
