package com.google.code.rees.scope.struts2.test;

import java.util.Map;
import java.util.Map.Entry;

import org.apache.struts2.dispatcher.Dispatcher;
import org.springframework.mock.web.MockHttpServletRequest;

import com.google.code.rees.scope.conversation.ConversationConstants;
import com.google.code.rees.scope.conversation.ConversationManager;
import com.google.code.rees.scope.conversation.ConversationUtil;
import com.google.code.rees.scope.conversation.DefaultConversationArbitrator;
import com.google.code.rees.scope.struts2.ConventionConstants;
import com.google.code.rees.scope.struts2.StrutsConversationConstants;
import com.opensymphony.xwork2.ActionContext;

/**
 * A utility for simplifying Struts2 unit testing against the interceptor stack
 * 
 * @author rees.byars
 */
public class ScopeTestUtil {

    private static ConversationManager manager;
    private static DefaultConversationArbitrator arbitrator = new DefaultConversationArbitrator();
    private static String actionSuffix;

    protected static ConversationManager getconversationManager() {
        if (manager == null) {
            manager = Dispatcher
                    .getInstance()
                    .getContainer()
                    .getInstance(ConversationManager.class,
                            StrutsConversationConstants.CONVERSATION_MANAGER_KEY);
        }
        return manager;
    }

    protected static String getActionSuffix() {
        if (actionSuffix == null) {
            actionSuffix = Dispatcher
                    .getInstance()
                    .getContainer()
                    .getInstance(String.class,
                            ConventionConstants.ACTION_SUFFIX);
        }
        return actionSuffix;
    }

    /**
     * For unit testing, sets the conversation IDs of the conversations in the
     * current thread
     * onto a given mock request.
     * 
     * @param request
     */
    public static void setConversationIdsOnRequest(
            MockHttpServletRequest request, Class<?> actionClass) {
        ActionContext actionContext = ActionContext.getContext();
        @SuppressWarnings("unchecked")
        Map<String, String> convoIdMap = ((Map<String, String>) actionContext
                .getValueStack().findValue(
                        StrutsConversationConstants.CONVERSATION_ID_MAP_STACK_KEY));
        if (convoIdMap != null) {
            for (Entry<String, String> entry : convoIdMap.entrySet()) {
                request.addParameter(entry.getKey(),
                        new String[] { entry.getValue() });
            }
        } else {
            for (String conversationName : arbitrator.getConversations(
                    actionClass, getActionSuffix())) {
                request.addParameter(
                        ConversationUtil.sanitizeName(conversationName)
                                + ConversationConstants.CONVERSATION_NAME_SESSION_MAP_SUFFIX,
                        conversationName + "-test-id");
            }
        }
    }

}
