package com.google.code.rees.scope.conversation;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class ConversationConfiguration {
	
	private Map<String, Field> fields;
	private Set<String> actionIds;
	private Set<String> beginActionIds;
	private Set<String> endActionIds;
	private String conversationName;
	
	public ConversationConfiguration(String conversationName) {
		fields = new HashMap<String, Field>();
		actionIds = new HashSet<String>();
		beginActionIds = new HashSet<String>();
		endActionIds = new HashSet<String>();
		this.conversationName = conversationName + ConversationConstants.CONVERSATION_NAME_SESSION_MAP_SUFFIX;
	}

	public void addField(String name, Field field) {
		fields.put(name, field);
	}
	
	public Map<String, Field> getFields() {
		return fields;
	}
	
	public void addAction(String actionId) {
		actionIds.add(actionId);
	}
	
	public void addBeginAction(String actionId) {
		beginActionIds.add(actionId);
	}
	
	public void addEndAction(String actionId) {
		endActionIds.add(actionId);
	}
	
	public boolean containsAction(String actionId) {
		return actionIds.contains(actionId);
	}
	
	public boolean isBeginAction(String actionId) {
		return beginActionIds.contains(actionId);
	}
	
	public boolean isEndAction(String actionId) {
		return endActionIds.contains(actionId);
	}
	
	public String getConversationName() {
		return conversationName;
	}
}
