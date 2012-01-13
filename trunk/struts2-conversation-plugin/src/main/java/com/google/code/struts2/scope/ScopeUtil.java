package com.google.code.struts2.scope;

import com.google.code.struts2.scope.conversation.ConversationUtil;
import com.google.code.struts2.scope.sessionfield.SessionFieldUtil;

public class ScopeUtil {
	
	public static void extractSessionFields(Object target) {
		SessionFieldUtil.extractSessionFields(target);
		ConversationUtil.extractConversationFields(target);
	}
	
	public static void injectScopeFields(Object target) {
		SessionFieldUtil.injectSessionFields(target);
		ConversationUtil.injectConversationFields(target);
	}
}
