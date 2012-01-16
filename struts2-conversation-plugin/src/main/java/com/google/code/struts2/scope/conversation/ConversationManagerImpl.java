package com.google.code.struts2.scope.conversation;

import java.lang.reflect.Field;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;

import org.apache.struts2.ServletActionContext;

import com.google.code.struts2.scope.ScopeUtil;
import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.ActionInvocation;
import com.opensymphony.xwork2.inject.Inject;
import com.opensymphony.xwork2.interceptor.PreResultListener;
import com.opensymphony.xwork2.util.ValueStack;
import com.opensymphony.xwork2.util.logging.Logger;
import com.opensymphony.xwork2.util.logging.LoggerFactory;

public class ConversationManagerImpl implements ConversationManager {
	
	private static final Logger LOG = LoggerFactory.getLogger(ConversationManagerImpl.class);

	protected Collection<ConversationConfig> conversationConfigs;
	protected ConversationConfigBuilder configBuilder;
	
	@Inject(ConversationConstants.CONFIG_BUILDER_KEY) 
	public void setConfigBuilder(ConversationConfigBuilder configBuilder) {
		this.configBuilder = configBuilder;
		conversationConfigs = configBuilder.getConversationConfigs().values();
	}
	
	@Override
	public Set<String> getAllConversationNames() {
		return configBuilder.getConversationConfigs().keySet();
	}

	@Override
	public void processConversationFields(ActionInvocation invocation) {
		for (ConversationConfig conversation : conversationConfigs) {
			processConversation(conversation, invocation);
		}
	}
	
	protected void processConversation(ConversationConfig conversation, ActionInvocation invocation) {
		String methodName = invocation.getProxy().getMethod();
		Map<String, Object> session = invocation.getInvocationContext().getSession();
		String conversationName = conversation.getConversationName();
		String conversationId = (String) ServletActionContext.getRequest().getParameter(conversationName);
		
		if (conversationId != null) {
			
			if (conversation.containsMethod(methodName)) {
				
				@SuppressWarnings("unchecked")
				Map<String, Object> conversationFieldValueMap = (Map<String, Object>) session.get(conversationId);

				if (conversationFieldValueMap != null) {
					Object action = invocation.getAction();
					if (LOG.isDebugEnabled()) {
						LOG.debug("In Conversation " + conversationName + ".  Setting Conversation Field values for method "
								+ methodName + " of class " + action.getClass());
					}
					Map<String, Field> classFields = conversation.getFields(action.getClass());
					if (classFields != null) {
						ScopeUtil.setFieldValues(action, classFields, conversationFieldValueMap);
					}
				}
				
				if (conversation.isEndMethod(methodName)) {
					if (LOG.isDebugEnabled()) {
						LOG.debug("In Conversation " + conversationName + ", removing conversation map from session following conversation end.");
					}
					session.remove(conversationId);
				} else {
					invocation.addPreResultListener(new ConversationResultListener(conversation, conversationId));
				}
			}
		} else if (conversation.isBeginMethod(methodName)) {
			if (LOG.isDebugEnabled()) {
				LOG.debug("Beginning new " + conversationName + " conversation.");
			}
			conversationId = java.util.UUID.randomUUID().toString();
			invocation.addPreResultListener(new ConversationResultListener(conversation, conversationId));
		}
	}

	@Override
	public void injectConversationFields(Object action) {
		Class<?> actionClass = action.getClass();
		synchronized (conversationConfigs) {
			conversationConfigs = configBuilder.addClassConfig(actionClass).values();
		}
		for (ConversationConfig conversation : conversationConfigs) {
			String conversationId = ConversationUtil.getConversationId(conversation.getConversationName());
			@SuppressWarnings("unchecked")
			Map<String, Object> conversationFieldValueMap = (Map<String, Object>) ActionContext.getContext().getSession().get(conversationId);
			if (conversationFieldValueMap != null) {
				Map<String, Field> classFields = conversation.getFields(action.getClass());
				if (classFields != null) {
					ScopeUtil.setFieldValues(action, classFields, conversationFieldValueMap);
				}
			}
		}
	}

	@Override
	public void extractConversationFields(Object action) {
		Class<?> actionClass = action.getClass();
		synchronized (conversationConfigs) {
			conversationConfigs = configBuilder.addClassConfig(actionClass).values();
		}
		for (ConversationConfig conversation : conversationConfigs) {
			
			Map<String, Field> classFieldMap = conversation.getFields(action.getClass());
			String conversationId = null;
			HttpServletRequest request = ServletActionContext.getRequest();
			
			if (request != null) {
				conversationId = (String) request.getParameter(conversation.getConversationName());
			}
			
			if (conversationId == null) {
				conversationId = java.util.UUID.randomUUID().toString();
			}
			
			if (classFieldMap != null) {
				
				Map<String, Object> session = ActionContext.getContext().getSession();
				Map<String, Object> conversationFieldValues = ScopeUtil.getFieldValues(action,
						classFieldMap);
				
				session.put(conversationId, conversationFieldValues);
			}
			ActionInvocation invocation = ActionContext.getContext().getActionInvocation();
			pushConversationIdOntoStack(conversation.getConversationName(), conversationId, invocation); 
		}
	}
	
	@SuppressWarnings("unchecked")
	protected static void pushConversationIdOntoStack(String conversationName, String conversationId, ActionInvocation invocation) {
		
		if (LOG.isDebugEnabled()) {
			LOG.debug("Setting flow ID of " + conversationId + " on stack for Conversation " + conversationName);
		}
		
		ValueStack stack = invocation.getStack();
		
		Map<String, String> conversationIdMap = (Map<String, String>) stack.findValue(ConversationConstants.CONVERSATION_ID_MAP_STACK_KEY);
		
		if (conversationIdMap == null) {
			Map<String, Map<String, String>> stackItem = new HashMap<String, Map<String, String>>();
			conversationIdMap = new HashMap<String, String>();
			stackItem.put(ConversationConstants.CONVERSATION_ID_MAP_STACK_KEY, conversationIdMap);
			stack.push(stackItem);
		}
		
		conversationIdMap.put(conversationName, conversationId);
		
	}

	class ConversationResultListener implements PreResultListener {

		private ConversationConfig conversation;
		private String conversationId;

		ConversationResultListener(ConversationConfig conversation, String conversationId) {
			this.conversation = conversation;
			this.conversationId = conversationId;
		}

		@Override
		public void beforeResult(ActionInvocation invocation, String resultCode) {
			
			Object action = invocation.getAction();
			
			Map<String, Field> classFieldMap = this.conversation.getFields(action.getClass());
			
			if (classFieldMap != null) {
				
				if (LOG.isDebugEnabled()) {
					LOG.debug("Getting conversation fields for Conversation " + conversation.getConversationName() 
							+ " following execution of action " + invocation.getProxy().getActionName());
				}
				
				Map<String, Object> session = invocation.getInvocationContext().getSession();
				@SuppressWarnings("unchecked")
				Map<String, Object> conversationFieldValues = (Map<String, Object>) session.get(conversationId);
				if (conversationFieldValues == null) {
					conversationFieldValues = new HashMap<String, Object>();
				}
				conversationFieldValues.putAll(ScopeUtil.getFieldValues(action,
						classFieldMap));
				
				session.put(this.conversationId, conversationFieldValues);
			}
			
			pushConversationIdOntoStack(conversation.getConversationName(), conversationId, invocation); 
			
		}
	}

}
