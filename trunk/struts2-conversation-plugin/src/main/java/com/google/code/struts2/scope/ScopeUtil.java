package com.google.code.struts2.scope;

import com.google.code.struts2.scope.conversation.ConversationUtil;
import com.google.code.struts2.scope.sessionfield.SessionField;
import com.google.code.struts2.scope.sessionfield.SessionFieldUtil;

/**
 * A utility class that provides static methods that are used internally and
 * for unit testing.  Usage of this utility in a outside of these contexts is
 * discouraged.  Most of the methods are not optimized for other uses.
 * 
 * @author rees.byars
 */
public class ScopeUtil {
	
	/**
	 * The current values of session and conversation fields that are annotated with 
	 * {@link SessionField} and {@link ConversationField} 
	 * are extracted from the target object and placed into the session
	 * and the active conversations available in the current thread. 
	 * 
	 * @param target
	 */
	public static void extractSessionFields(Object target) {
		SessionFieldUtil.extractSessionFields(target);
		ConversationUtil.extractConversationFields(target);
	}
	
	/**
	 * The target object's session and conversation fields that are annotated with 
	 * {@link SessionField} and {@link ConversationField} are injected from the session
	 * and the active conversations available in the current thread. 
	 * 
	 * @param target
	 */
	public static void injectScopeFields(Object target) {
		SessionFieldUtil.injectSessionFields(target);
		ConversationUtil.injectConversationFields(target);
	}
}
