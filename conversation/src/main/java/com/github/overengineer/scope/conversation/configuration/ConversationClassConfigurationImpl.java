package com.github.overengineer.scope.conversation.configuration;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import com.github.overengineer.scope.conversation.ConversationConstants;
import com.github.overengineer.scope.conversation.ConversationUtil;

/**
 * the  classic java map-wrapping bean with verbose data-struct-oriented internal classes.
 * 
 * @author reesbyars
 *
 */
public class ConversationClassConfigurationImpl implements ConversationClassConfiguration {
	
	private final Map<String, Field> fields = new HashMap<String, Field>();
    private final Set<String> actions = new HashSet<String>();
    private final Map<String, BeginConfig> beginActions = new HashMap<String, BeginConfig>();
    private final Map<String, Boolean> endActions  = new HashMap<String, Boolean>();
    private final String conversationName;

    protected ConversationClassConfigurationImpl(String conversationName) { 
        this.conversationName = ConversationUtil.sanitizeName(conversationName) + ConversationConstants.CONVERSATION_NAME_SUFFIX;
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
        actions.add(actionId);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void addBeginAction(String actionId, long maxIdleTimeMillis, String maxIdleTime, int maxInstances, boolean transactional) {
        this.addAction(actionId);
        beginActions.put(actionId, new BeginConfig(maxIdleTimeMillis, maxIdleTime, maxInstances, transactional));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void addEndAction(String actionId, boolean endAfterView) {
        endActions.put(actionId, endAfterView);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean containsAction(String actionId) {
        return actions.contains(actionId);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isBeginAction(String actionId) {
        return beginActions.containsKey(actionId);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isEndAction(String actionId) {
        return endActions.containsKey(actionId);
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
	public boolean endAfterView(String actionId) {
		return endActions.get(actionId);
	}
    
    /**
     * {@inheritDoc}
     */
    @Override
    public long getMaxIdleTime(String beginActionId) {
    	return beginActions.get(beginActionId).maxIdleTimeMillis;
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
	public String getMaxIdleTimeExpression(String beginActionId) {
		return beginActions.get(beginActionId).maxIdleTime;
	}
    
    /**
     * {@inheritDoc}
     */
    @Override
    public int getMaxInstances(String beginActionId) {
    	return beginActions.get(beginActionId).maxInstances;
    }

	/**
     * {@inheritDoc}
     */
	@Override
	public boolean isTransactional(String beginActionId) {
		return beginActions.get(beginActionId).transactional;
	}

    /**
     * {@inheritDoc}
     */
    @Override
    public String getConversationName() {
        return conversationName;
    }
    
    /**
     * 
     * yuck....  TODO
     *
     */
    class BeginConfig {
    	long maxIdleTimeMillis;
    	String maxIdleTime;
    	int maxInstances;
    	boolean transactional;
    	BeginConfig(long maxIdleTimeMillis, String maxIdleTime, int maxInstances, boolean transactional) {
    		this.maxIdleTimeMillis = maxIdleTimeMillis;
    		this.maxIdleTime = maxIdleTime;
    		this.maxInstances = maxInstances;
    		this.transactional = transactional;
    	}
    }

}
