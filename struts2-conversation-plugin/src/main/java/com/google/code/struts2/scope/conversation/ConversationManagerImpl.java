package com.google.code.struts2.scope.conversation;

import java.lang.reflect.Field;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

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

	protected Map<Class<?>, Collection<ConversationConfig>> conversationConfigs;
	protected ConversationConfigBuilder configBuilder;
	
	@Inject(ConversationConstants.CONFIG_BUILDER_KEY) 
	public void setConfigBuilder(ConversationConfigBuilder configBuilder) {
		this.configBuilder = configBuilder;
		conversationConfigs = configBuilder.getConversationConfigs();
	}

	@Override
	public void processConversations(ActionInvocation invocation) {
		Object action = invocation.getAction();
		Collection<ConversationConfig> actionConversationConfigs = this.conversationConfigs.get(action.getClass());
		if (actionConversationConfigs != null) {
			for (ConversationConfig conversationConfig : actionConversationConfigs) {
				processConversation(conversationConfig, invocation, action);
			}
		}
	}
	
	protected void processConversation(ConversationConfig conversationConfig, ActionInvocation invocation, Object action) {
		String methodName = invocation.getProxy().getMethod();
		Map<String, Object> session = invocation.getInvocationContext().getSession();
		String conversationName = conversationConfig.getConversationName();
		String conversationId = (String) ServletActionContext.getRequest().getParameter(conversationName);
		
		if (conversationId != null) {
			
			if (conversationConfig.containsAction(methodName)) {
				
				@SuppressWarnings("unchecked")
				Map<String, Object> conversationContext = (Map<String, Object>) session.get(conversationId);

				if (conversationContext != null) {
					if (LOG.isDebugEnabled()) {
						LOG.debug("In Conversation " + conversationName + ".  Setting Conversation Field values for method "
								+ methodName + " of class " + action.getClass());
					}
					Map<String, Field> classFields = conversationConfig.getFields();
					if (classFields != null) {
						ScopeUtil.setFieldValues(action, classFields, conversationContext);
					}
				}
				
				if (conversationConfig.isEndAction(methodName)) {
					if (LOG.isDebugEnabled()) {
						LOG.debug("In Conversation " + conversationName + ", removing conversation map from session following conversation end.");
					}
					session.remove(conversationId);
				} else {
					invocation.addPreResultListener(new ConversationResultListener(conversationConfig, conversationId));
				}
			}
		} else if (conversationConfig.isBeginAction(methodName)) {
			if (LOG.isDebugEnabled()) {
				LOG.debug("Beginning new " + conversationName + " conversation.");
			}
			conversationId = java.util.UUID.randomUUID().toString();
			invocation.addPreResultListener(new ConversationResultListener(conversationConfig, conversationId));
		}
	}

	@Override
	public void injectConversationFields(Object action) {
		Class<?> actionClass = action.getClass();
		synchronized (conversationConfigs) {
			this.configBuilder.addClassConfig(actionClass);
			this.conversationConfigs = this.configBuilder.getConversationConfigs();
		}
		Collection<ConversationConfig> actionConversationConfigs = this.conversationConfigs.get(action.getClass());
		if (actionConversationConfigs != null) {
			Map<String, Object> session = ActionContext.getContext().getSession();
			for (ConversationConfig conversation : actionConversationConfigs) {
				String conversationId = ConversationUtil.getConversationId(conversation.getConversationName());
				@SuppressWarnings("unchecked")
				Map<String, Object> conversationContext = (Map<String, Object>) session.get(conversationId);
				if (conversationContext != null) {
					Map<String, Field> cachedConversationFields = conversation.getFields();
					if (cachedConversationFields != null) {
						ScopeUtil.setFieldValues(action, cachedConversationFields, conversationContext);
					}
				}
			}
		}
	}

	@Override
	public void extractConversationFields(Object action) {
		Class<?> actionClass = action.getClass();
		synchronized (conversationConfigs) {
			this.configBuilder.addClassConfig(actionClass);
			this.conversationConfigs = this.configBuilder.getConversationConfigs();
		}
		Collection<ConversationConfig> actionConversationConfigs = this.conversationConfigs.get(action.getClass());
		if (actionConversationConfigs != null) {
			for (ConversationConfig conversation : actionConversationConfigs) {
				
				Map<String, Field> cachedConversationFields = conversation.getFields();
				String conversationId = null;
				String conversationName = conversation.getConversationName();
				HttpServletRequest request = ServletActionContext.getRequest();
				
				if (request != null) {
					conversationId = (String) request.getParameter(conversationName);
				}
				
				if (conversationId == null) {
					conversationId = java.util.UUID.randomUUID().toString();
				}
				
				ActionContext actionContext = ActionContext.getContext();
				if (cachedConversationFields != null) {
					
					Map<String, Object> session = actionContext.getSession();
					Map<String, Object> conversationContext = ScopeUtil.getFieldValues(action,
							cachedConversationFields);
					
					session.put(conversationId, conversationContext);
				}
				ActionInvocation invocation = actionContext.getActionInvocation();
				pushConversationIdOntoStack(conversationName, conversationId, invocation); 
			}
		}
	}
	
	@SuppressWarnings("unchecked")
	protected static void pushConversationIdOntoStack(String conversationName, String conversationId, ActionInvocation invocation) {
		
		if (LOG.isDebugEnabled()) {
			LOG.debug("Setting flow ID of " + conversationId + " on stack for Conversation " + conversationName);
		}
		
		ValueStack stack = invocation.getStack();
		
		Map<String, String> stackConversationIds = (Map<String, String>) stack.findValue(ConversationConstants.CONVERSATION_ID_MAP_STACK_KEY);
		
		if (stackConversationIds == null) {
			Map<String, Map<String, String>> stackItem = new HashMap<String, Map<String, String>>();
			stackConversationIds = new HashMap<String, String>();
			stackItem.put(ConversationConstants.CONVERSATION_ID_MAP_STACK_KEY, stackConversationIds);
			stack.push(stackItem);
		}
		
		stackConversationIds.put(conversationName, conversationId);
		
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
			
			Map<String, Field> cachedConversationFields = this.conversation.getFields();
			
			if (cachedConversationFields != null) {
				
				if (LOG.isDebugEnabled()) {
					LOG.debug("Getting conversation fields for Conversation " + conversation.getConversationName() 
							+ " following execution of action " + invocation.getProxy().getActionName());
				}
				
				Map<String, Object> session = invocation.getInvocationContext().getSession();
				@SuppressWarnings("unchecked")
				Map<String, Object> conversationContext = (Map<String, Object>) session.get(conversationId);
				if (conversationContext == null) {
					conversationContext = new HashMap<String, Object>();
				}
				conversationContext.putAll(ScopeUtil.getFieldValues(action,cachedConversationFields));
				
				session.put(this.conversationId, conversationContext);
			}
			
			pushConversationIdOntoStack(conversation.getConversationName(), conversationId, invocation); 
			
		}
	}

}
