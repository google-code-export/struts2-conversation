package com.google.code.struts2.scope.conversation;

import java.lang.reflect.Field;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.apache.struts2.ServletActionContext;

import com.google.code.struts2.scope.AbstractScopeInterceptor;
import com.opensymphony.xwork2.ActionInvocation;
import com.opensymphony.xwork2.inject.Inject;
import com.opensymphony.xwork2.interceptor.PreResultListener;
import com.opensymphony.xwork2.util.ValueStack;
import com.opensymphony.xwork2.util.logging.Logger;
import com.opensymphony.xwork2.util.logging.LoggerFactory;

/**
 * 
 * @author rees.byars
 */
public class ConversationInterceptor extends AbstractScopeInterceptor {

	private static final long serialVersionUID = 4489190504064841648L;

	private static final Logger LOG = LoggerFactory
			.getLogger(ConversationInterceptor.class);

	protected ConversationConfigBuilder configBuilder;
	protected Collection<ConversationConfig> conversationConfigs;
	
	@Inject(ConversationConstants.CONFIG_BUILDER_KEY)
	public void setConversationConfigBuilder(ConversationConfigBuilder configBuilder) {
		this.configBuilder = configBuilder;
	}

	@Override
	public void init() {
		LOG.info("Initializing the ConversationInterceptor...");
		if (configBuilder == null) {
			LOG.error("No ConversationConfigBuilder was found.  " +
					"Please make sure that a bean named " + 
					ConversationConstants.CONFIG_BUILDER_KEY +
					" of type " + ConversationConfigBuilder.class.getName() + 
					" is defined in a configuration file such as struts.xml.");
		} else {
			conversationConfigs = configBuilder.getConversationConfigs().values();
		}
	}

	public String intercept(ActionInvocation invocation) throws Exception {
		for (ConversationConfig conversation : conversationConfigs) {
			beforeInvocation(conversation, invocation);
		}
		return invocation.invoke();
	}

	@SuppressWarnings("unchecked")
	protected void beforeInvocation(ConversationConfig conversation,
			ActionInvocation invocation) {

		String methodName = invocation.getProxy().getMethod();
		Map<String, Object> session = invocation.getInvocationContext().getSession();
		String conversationName = conversation.getConversationName();
		String conversationId = (String) ServletActionContext.getRequest().getParameter(conversationName);
		
		if (conversationId != null) {
			
			if (!conversation.containsMethod(methodName)) {
				if (LOG.isDebugEnabled()) {
					LOG.debug("In Conversation " + conversationName + ", removing conversation map from session.");
				}
				session.remove(conversationId);
			} else {
				
				Map<String, Object> conversationFieldValueMap = (Map<String, Object>) session.get(conversationId);

				if (conversationFieldValueMap != null) {
					Object action = invocation.getAction();
					if (LOG.isDebugEnabled()) {
						LOG.debug("In Conversation " + conversationName + ".  Setting Conversation Field values for method "
								+ methodName + " of class " + action.getClass());
					}
					Map<String, Field> classFields = conversation.getFields(action.getClass());
					if (classFields != null) {
						setFieldValues(action, classFields, conversationFieldValueMap);
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
				Map<String, Object> conversationFieldValues = getFieldValues(action,
						classFieldMap);
				
				session.put(this.conversationId, conversationFieldValues);
			}
			
			pushConversationIdOntoStack(conversation.getConversationName(), conversationId, invocation); 
			
		}
	}

}
