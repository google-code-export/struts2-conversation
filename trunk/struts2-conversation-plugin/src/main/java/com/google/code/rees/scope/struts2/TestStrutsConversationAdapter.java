package com.google.code.rees.scope.struts2;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.struts2.ServletActionContext;

import com.google.code.rees.scope.conversation.ConversationAdapter;
import com.google.code.rees.scope.conversation.ConversationConfiguration;
import com.google.code.rees.scope.conversation.ConversationPostProcessor;
import com.google.code.rees.scope.util.RequestContextUtil;

public class TestStrutsConversationAdapter extends ConversationAdapter {
	
	Object action;
	String actionId;
	HttpServletRequest request;
	Map<String, Object> session;
	
	public TestStrutsConversationAdapter(HttpServletRequest request, Map<String, Object> session) {
		this.request = request;
		this.session = session;
	}
	
	@Override
	public Map<String, Object> getSessionContext() {
		return session;
	}
	
	@Override
	public Map<String, String> getRequestContext() {
		return RequestContextUtil.getRequestContext(ServletActionContext.getRequest());
	}

	@Override
	public Object getAction() {
		return this.action;
	}
	
	public void setAction(Object action) {
		this.action = action;
	}

	@Override
	public String getActionId() {
		return this.actionId;
	}
	
	public void setActionId(String actionId) {
		this.actionId = actionId;
	}

	@Override
	public void dispatchPostProcessor(ConversationPostProcessor postProcessor,
			ConversationConfiguration conversationConfig, String conversationId) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void addConversation(String conversationName, String conversationId) {
		// TODO Auto-generated method stub
		
	}

}
