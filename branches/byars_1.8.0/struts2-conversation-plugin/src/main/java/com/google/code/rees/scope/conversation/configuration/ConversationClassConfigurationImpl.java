package com.google.code.rees.scope.conversation.configuration;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import com.google.code.rees.scope.conversation.ConversationConstants;
import com.google.code.rees.scope.conversation.ConversationUtil;

/**
 * 
 * @author reesbyars
 *
 */
public class ConversationClassConfigurationImpl implements ConversationClassConfiguration {
	
	private Map<String, Field> fields;
    private Set<String> actionIds;
    private Set<String> beginActionIds;
    private Set<String> endActionIds;
    private Map<String, Long> beginActionIdleTimes;
    private String conversationName;

    public ConversationClassConfigurationImpl(String conversationName) {
        fields = new HashMap<String, Field>();
        actionIds = new HashSet<String>();
        beginActionIds = new HashSet<String>();
        endActionIds = new HashSet<String>();
        beginActionIdleTimes = new HashMap<String, Long>();
        this.conversationName = ConversationUtil
                .sanitizeName(conversationName)
                + ConversationConstants.CONVERSATION_NAME_SUFFIX;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void addField(String name, Field field) {
        fields.put(name, field);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Map<String, Field> getFields() {
        return fields;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void addAction(String actionId) {
        actionIds.add(actionId);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void addBeginAction(String actionId, Long maxIdleTimeMillis) {
        actionIds.add(actionId);
        beginActionIds.add(actionId);
        beginActionIdleTimes.put(actionId, maxIdleTimeMillis);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void addEndAction(String actionId) {
        actionIds.add(actionId);
        endActionIds.add(actionId);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean containsAction(String actionId) {
        return actionIds.contains(actionId);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isBeginAction(String actionId) {
        return beginActionIds.contains(actionId);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isEndAction(String actionId) {
        return endActionIds.contains(actionId);
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public long getMaxIdleTime(String beginActionId) {
    	return beginActionIdleTimes.get(beginActionId);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getConversationName() {
        return conversationName;
    }

}
