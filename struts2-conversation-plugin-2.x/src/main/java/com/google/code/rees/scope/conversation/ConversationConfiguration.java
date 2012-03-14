package com.google.code.rees.scope.conversation;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

/**
 * This class is used to cache the action IDs for a single
 * class for a single conversation.
 * 
 * @see {@link ConversationConfigurationProvider}
 * @see {@link ConversationManager}
 * 
 * @author rees.byars
 * 
 */
public class ConversationConfiguration implements Serializable {

    private static final long serialVersionUID = 9136235407036867477L;
    private Set<String> actionIds;
    private Set<String> beginActionIds;
    private Set<String> endActionIds;
    private String conversationName;

    public ConversationConfiguration(String conversationName) {
        actionIds = new HashSet<String>();
        beginActionIds = new HashSet<String>();
        endActionIds = new HashSet<String>();
        this.conversationName = ConversationUtil.sanitizeName(conversationName)
                + ConversationConstants.CONVERSATION_NAME_SESSION_MAP_SUFFIX;
    }

    /**
     * Add an actionId for an intermediate action
     * 
     * @see {@link ConversationAdapter#getActionId()}
     * @see {@link ConversationArbitrator#getName(Method)}
     * @param actionId
     */
    public void addAction(String actionId) {
        actionIds.add(actionId);
    }

    /**
     * Add an actionId for a begin action
     * 
     * @see {@link ConversationAdapter#getActionId()}
     * @see {@link ConversationArbitrator#getName(Method)}
     * @param actionId
     */
    public void addBeginAction(String actionId) {
        actionIds.add(actionId);
        beginActionIds.add(actionId);
    }

    /**
     * Add an actionId for an end action
     * 
     * @see {@link ConversationAdapter#getActionId()}
     * @see {@link ConversationArbitrator#getName(Method)}
     * @param actionId
     */
    public void addEndAction(String actionId) {
        actionIds.add(actionId);
        endActionIds.add(actionId);
    }

    /**
     * Indicates whether the actionId identifies the action as an intermediate
     * member for this conversation
     * 
     * @see {@link ConversationAdapter#getActionId()}
     * @see {@link ConversationArbitrator#getName(Method)}
     * @param actionId
     * @return
     */
    public boolean containsAction(String actionId) {
        return actionIds.contains(actionId);
    }

    /**
     * Indicates whether the actionId identifies the action as a begin
     * member for this conversation
     * 
     * @see {@link ConversationAdapter#getActionId()}
     * @see {@link ConversationArbitrator#getName(Method)}
     * @param actionId
     * @return
     */
    public boolean isBeginAction(String actionId) {
        return beginActionIds.contains(actionId);
    }

    /**
     * Indicates whether the actionId identifies the action as an end
     * member for this conversation
     * 
     * @see {@link ConversationAdapter#getActionId()}
     * @see {@link ConversationArbitrator#getName(Method)}
     * @param actionId
     * @return
     */
    public boolean isEndAction(String actionId) {
        return endActionIds.contains(actionId);
    }

    /**
     * Returns the name of the conversation that this configuration is for
     * 
     * @return
     */
    public String getConversationName() {
        return conversationName;
    }
}
