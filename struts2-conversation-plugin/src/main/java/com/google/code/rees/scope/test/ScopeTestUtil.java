package com.google.code.rees.scope.test;

import java.util.Map;
import java.util.Map.Entry;

import org.apache.struts2.dispatcher.Dispatcher;
import org.springframework.mock.web.MockHttpServletRequest;

import com.google.code.rees.scope.conversation.ConversationAdapter;
import com.google.code.rees.scope.conversation.ConversationConstants;
import com.google.code.rees.scope.conversation.ConversationManager;
import com.google.code.rees.scope.sessionfield.SessionFieldUtil;
import com.google.code.rees.scope.struts2.StrutsConversationAdapter;
import com.google.code.rees.scope.struts2.StrutsConversationConfigBuilder;
import com.google.code.rees.scope.struts2.StrutsScopeConstants;
import com.opensymphony.xwork2.ActionContext;

public class ScopeTestUtil {
	
	private static ConversationManager manager;
	
	protected static ConversationManager getconversationManager() {
		if (manager == null) {
			manager = Dispatcher.getInstance().getContainer().getInstance(ConversationManager.class, StrutsScopeConstants.MANAGER_KEY);
		}
		return manager;
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
			for (String c : StrutsConversationConfigBuilder.getConversationNames(actionClass)) {
				request.addParameter(c + ConversationConstants.CONVERSATION_NAME_SESSION_MAP_SUFFIX, c + "-test-id");
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
            SessionFieldUtil.extractSessionFields(target);
            ConversationAdapter adapater = new StrutsConversationAdapter(ActionContext.getContext().getActionInvocation());
            getconversationManager().extractConversationFields(adapater);
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
            ConversationAdapter adapater = new StrutsConversationAdapter(ActionContext.getContext().getActionInvocation());
            getconversationManager().injectConversationFields(adapater);
    }

}
