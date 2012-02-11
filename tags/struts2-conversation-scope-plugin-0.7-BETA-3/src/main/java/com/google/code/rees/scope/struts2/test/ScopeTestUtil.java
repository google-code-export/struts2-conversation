package com.google.code.rees.scope.struts2.test;

import java.util.Map;
import java.util.Map.Entry;

import org.apache.struts2.dispatcher.Dispatcher;
import org.springframework.mock.web.MockHttpServletRequest;

import com.google.code.rees.scope.conversation.ConversationAdapter;
import com.google.code.rees.scope.conversation.ConversationConstants;
import com.google.code.rees.scope.conversation.ConversationManager;
import com.google.code.rees.scope.conversation.ConversationUtil;
import com.google.code.rees.scope.conversation.DefaultConversationArbitrator;
import com.google.code.rees.scope.conversation.InjectionConversationManager;
import com.google.code.rees.scope.session.SessionUtil;
import com.google.code.rees.scope.struts2.ConventionConstants;
import com.google.code.rees.scope.struts2.StrutsScopeConstants;
import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.util.ValueStack;

public class ScopeTestUtil {
	
	private static InjectionConversationManager manager;
	private static DefaultConversationArbitrator arbitrator = new DefaultConversationArbitrator();
	private static String actionSuffix;
	
	protected static InjectionConversationManager getconversationManager() {
		if (manager == null) {
			manager = (InjectionConversationManager) Dispatcher.getInstance().getContainer().getInstance(ConversationManager.class, StrutsScopeConstants.CONVERSATION_MANAGER_KEY);
		}
		return manager;
	}
	
	protected static String getActionSuffix() {
		if (actionSuffix == null) {
			actionSuffix = Dispatcher.getInstance().getContainer().getInstance(String.class, ConventionConstants.ACTION_SUFFIX);
		}
		return actionSuffix;
	}
	
	/**
	 * Given a conversation name, returns the ID of the conversation for the currently
	 * executing thread.  
	 * 
	 * @param conversationName
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static String getConversationId(String conversationName) {
		String id = ConversationUtil.getConversationId(conversationName);
		if (id == null) {
			ValueStack stack = ActionContext.getContext().getValueStack();
			id = (String) ((Map<String, Object>) stack.findValue(StrutsScopeConstants.CONVERSATION_ID_MAP_STACK_KEY)).get(conversationName);
		}
		return id;
	}

	/**
	 * For unit testing, sets the conversation IDs of the conversations in the current thread
	 * onto a given mock request.
	 * 
	 * @param request
	 */
	public static void setConversationIdsOnRequest(MockHttpServletRequest request, Class<?> actionClass) {
		ActionContext actionContext = ActionContext.getContext();
		@SuppressWarnings("unchecked")
		Map<String, String> convoIdMap = ((Map<String, String>) actionContext.getValueStack()
				.findValue(StrutsScopeConstants.CONVERSATION_ID_MAP_STACK_KEY));
		if (convoIdMap != null) {
			for (Entry<String, String> entry : convoIdMap.entrySet()) {
				request.addParameter(entry.getKey(), new String[]{entry.getValue()});
			}
		} else {
			for (String conversationName : arbitrator.getConversations(actionClass, getActionSuffix())) {
				request.addParameter(ConversationUtil.sanitizeConversationName(conversationName) + ConversationConstants.CONVERSATION_NAME_SESSION_MAP_SUFFIX, conversationName + "-test-id");
			}
		}
	}
	
	/**
     * The current values of session and conversation fields that are annotated with 
     * {@link SessionField} and {@link ConversationField} 
     * are extracted from the target object and placed into the session
     * and the active conversations available in the current thread. 
     * 
     * @param target
     */
    public static void extractScopeFields(Object target) {
            SessionUtil.extractSessionFields(target);
            getconversationManager().extractConversationFields(target, ConversationAdapter.getAdapter());
    }
    
    /**
     * The target object's session and conversation fields that are annotated with 
     * {@link SessionField} and {@link ConversationField} are injected from the session
     * and the active conversations available in the current thread. 
     * 
     * @param target
     */
    public static void injectScopeFields(Object target) {
            SessionUtil.injectSessionFields(target);
            getconversationManager().injectConversationFields(target, ConversationAdapter.getAdapter());
    }

}
