package com.google.code.rees.scope.spring;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;

import org.springframework.web.method.HandlerMethod;

import com.google.code.rees.scope.conversation.ConversationAdapter;
import com.google.code.rees.scope.conversation.ConversationConfiguration;
import com.google.code.rees.scope.conversation.ConversationContextFactory;
import com.google.code.rees.scope.conversation.ConversationPostProcessor;
import com.google.code.rees.scope.conversation.MonitoredConversationContextFactory;
import com.google.code.rees.scope.util.RequestContextUtil;
import com.google.code.rees.scope.util.SessionContextUtil;

public class SpringConversationAdapter extends ConversationAdapter {

	private static final long serialVersionUID = 5664922664767226366L;
	
	protected final static ConversationContextFactory conversationContextFactory 
		= new MonitoredConversationContextFactory();
	
	protected Map<String, Object> sessionContext;
	protected Map<String, String> requestContext;
	protected Map<String, Object> viewContext;
	protected Object action;
	protected String actionId;
	protected Set<SpringConversationPostProcessorWrapper> postProcessors;
	
	public SpringConversationAdapter(HttpServletRequest request, HandlerMethod handler) {
		this.sessionContext = SessionContextUtil.getSessionContext(request);
		this.requestContext = RequestContextUtil.getRequestContext(request);
		this.action = handler.getBean();
		this.actionId = handler.getMethod().getName();
		this.postProcessors = new HashSet<SpringConversationPostProcessorWrapper>();
		this.viewContext = new HashMap<String, Object>();
	}

	@Override
	public Map<String, Object> createConversationContext(String conversationId, Map<String, Object> sessionContext) {
		return conversationContextFactory.createConversationContext(conversationId, sessionContext);
	}

	@Override
	public Object getAction() {
		return this.action;
	}

	@Override
	public String getActionId() {
		return this.actionId;
	}

	@Override
	public Map<String, Object> getSessionContext() {
		return this.sessionContext;
	}

	@Override
	public Map<String, String> getRequestContext() {
		return this.requestContext;
	}

	@Override
	public void dispatchPostProcessor(ConversationPostProcessor postProcessor, ConversationConfiguration conversationConfig, String conversationId) {
		this.postProcessors.add(new SpringConversationPostProcessorWrapper(this, postProcessor, conversationConfig, conversationId));
	}

	@Override
	public void addConversation(String conversationName, String conversationId) {
		this.viewContext.put(conversationName, conversationId);
	}
	
	public Collection<SpringConversationPostProcessorWrapper> getPostProcessors() {
		return this.postProcessors;
	}
	
	public Map<String, Object> getViewContext() {
		return this.viewContext;
	}
	
	public static SpringConversationAdapter getSpringAdapter() {
		return (SpringConversationAdapter) getAdapter();
	}

}
