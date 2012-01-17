package com.google.code.struts2.scope.conversation;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class ConversationConfig {
	
	private Map<String, Field> fields;
	private Set<String> actionMethodNames;
	private Set<String> beginActionMethodNames;
	private Set<String> endActionMethodNames;
	private String conversationName;
	
	public ConversationConfig(String conversationName) {
		fields = new HashMap<String, Field>();
		actionMethodNames = new HashSet<String>();
		beginActionMethodNames = new HashSet<String>();
		endActionMethodNames = new HashSet<String>();
		this.conversationName = conversationName + ConversationConstants.CONVERSATION_NAME_SESSION_MAP_SUFFIX;
	}

	public void addField(String name, Field field) {
		fields.put(name, field);
	}
	
	public Map<String, Field> getFields() {
		return fields;
	}
	
	public void addAction(Method method) {
		actionMethodNames.add(method.getName());
	}
	
	public void addBeginAction(Method method) {
		beginActionMethodNames.add(method.getName());
	}
	
	public void addEndAction(Method method) {
		endActionMethodNames.add(method.getName());
	}
	
	public boolean containsAction(String methodName) {
		return actionMethodNames.contains(methodName);
	}
	
	public boolean isBeginAction(String methodName) {
		return beginActionMethodNames.contains(methodName);
	}
	
	public boolean isEndAction(String methodName) {
		return endActionMethodNames.contains(methodName);
	}
	
	public String getConversationName() {
		return conversationName;
	}
}
