package com.google.code.struts2.scope.conversation;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class ConversationConfig {
	
	private Map<Class<?>, Map<String, Field>> fields;
	private Set<String> methods;
	private Set<String> beginMethods;
	private Set<String> endMethods;
	private String conversationName;
	
	public ConversationConfig(String conversationName) {
		fields = new HashMap<Class<?>, Map<String, Field>>();
		methods = new HashSet<String>();
		beginMethods = new HashSet<String>();
		endMethods = new HashSet<String>();
		this.conversationName = conversationName + ConversationConstants.CONVERSATION_NAME_SESSION_MAP_SUFFIX;
	}

	public void addField(Class<?> clazz, String name, Field field) {
		Map<String, Field> classFields = fields.get(clazz);
		if (classFields == null) {
			classFields = new HashMap<String, Field>();
		}
		classFields.put(name, field);
		fields.put(clazz, classFields);
	}
	
	public Map<String, Field> getFields(Class<?> clazz) {
		return fields.get(clazz);
	}
	
	public void addMethod(Method method) {
		methods.add(method.getName());
	}
	
	public void addBeginMethod(Method method) {
		beginMethods.add(method.getName());
	}
	
	public void addEndMethod(Method method) {
		endMethods.add(method.getName());
	}
	
	public boolean containsMethod(String method) {
		return methods.contains(method);
	}
	
	public boolean isBeginMethod(String method) {
		return beginMethods.contains(method);
	}
	
	public boolean isEndMethod(String method) {
		return endMethods.contains(method);
	}
	
	public String getConversationName() {
		return conversationName;
	}
}
