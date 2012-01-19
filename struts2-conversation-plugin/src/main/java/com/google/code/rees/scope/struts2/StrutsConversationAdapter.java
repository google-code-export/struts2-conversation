package com.google.code.rees.scope.struts2;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.struts2.StrutsStatics;

import com.google.code.rees.scope.conversation.ConversationAdapter;
import com.google.code.rees.scope.conversation.ConversationConfig;
import com.google.code.rees.scope.conversation.ConversationPostProcessor;
import com.google.code.rees.scope.util.RequestContextUtil;
import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.ActionInvocation;
import com.opensymphony.xwork2.interceptor.PreResultListener;

import com.opensymphony.xwork2.util.ValueStack;

public class StrutsConversationAdapter extends ConversationAdapter {

	protected ActionInvocation invocation;
	protected ActionContext actionContext;
	protected Map<String, String> requestContext;
	protected ValueStack valueStack;

	public StrutsConversationAdapter(ActionInvocation invocation) {
		this.invocation = invocation;
		this.actionContext = invocation.getInvocationContext();
		this.valueStack = invocation.getStack();
	}

	@Override
	public Object getAction() {
		return invocation.getAction();
	}

	@Override
	public String getActionId() {
		return invocation.getProxy().getMethod();
	}

	@Override
	public Map<String, Object> getSessionContext() {
		return this.actionContext.getSession();
	}
	
	@Override
	public Map<String, String> getRequestContext() {
		if (requestContext == null) {
			HttpServletRequest request = (HttpServletRequest) 
				this.actionContext.get(StrutsStatics.HTTP_REQUEST);
			requestContext = RequestContextUtil.getRequestContext(request);
		}
		return requestContext;
	}

	@Override
	public void dispatchPostProcessor(ConversationPostProcessor postProcessor,
			ConversationConfig conversationConfig, String conversationId) {
		invocation.addPreResultListener(new ConversationResultListener(this,
				postProcessor, conversationConfig, conversationId));
	}

	@Override
	public void addConversation(String conversationName, String conversationId) {

		@SuppressWarnings("unchecked")
		Map<String, String> stackConversationIds = (Map<String, String>) this.valueStack
				.findValue(StrutsScopeConstants.CONVERSATION_ID_MAP_STACK_KEY);

		if (stackConversationIds == null) {
			Map<String, Map<String, String>> stackItem = new HashMap<String, Map<String, String>>();
			stackConversationIds = new HashMap<String, String>();
			stackItem.put(StrutsScopeConstants.CONVERSATION_ID_MAP_STACK_KEY,
					stackConversationIds);
			this.valueStack.push(stackItem);
		}

		stackConversationIds.put(conversationName, conversationId);
	}

	class ConversationResultListener implements PreResultListener {

		private ConversationConfig conversationConfig;
		private String conversationId;
		private ConversationPostProcessor postProcessor;
		private ConversationAdapter conversationAdapter;

		ConversationResultListener(ConversationAdapter conversationAdapter,
				ConversationPostProcessor postProcessor,
				ConversationConfig conversationConfig, String conversationId) {
			this.conversationAdapter = conversationAdapter;
			this.postProcessor = postProcessor;
			this.conversationConfig = conversationConfig;
			this.conversationId = conversationId;
		}

		@Override
		public void beforeResult(ActionInvocation invocation, String resultCode) {
			this.postProcessor.postProcessConversation(conversationAdapter,
					conversationConfig, conversationId);
		}
	}

}