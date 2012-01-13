package com.google.code.struts2.scope.ui;

import java.util.Map;

import org.apache.struts2.views.jsp.URLTag;

import com.google.code.struts2.scope.conversation.ConversationConstants;

public class ConversationUrlTag extends URLTag {

	private static final long serialVersionUID = -2799594627916112974L;
	
	@Override
	protected void populateParams() {
		super.populateParams();
		@SuppressWarnings("unchecked")
		Map<String, String> convoIdMap = 
			(Map<String, String>) this.component.getStack().findValue(ConversationConstants.CONVERSATION_ID_MAP_STACK_KEY);
		this.component.addAllParameters(convoIdMap);
	}

}