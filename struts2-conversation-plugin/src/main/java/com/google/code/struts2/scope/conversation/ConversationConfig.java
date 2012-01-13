package com.google.code.struts2.scope.conversation;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class ConversationConfig {
	
	private Map<Class<?>, Map<String, Field>> fields;
	//private Map<Class<?>, Map<String, Method>> setters;
	//private Map<Class<?>, Map<String, Method>> getters;
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
		classFields.put(ConversationUtil.buildKey(name, field.getType()), field);
		fields.put(clazz, classFields);
	}
	
	public Map<String, Field> getFields(Class<?> clazz) {
		return fields.get(clazz);
	}
	
	/*
	public void addSetterMethod(Class<?> actionClass, Class<?> fieldClass, String fieldName, Method method) {
		Map<String, Method> classSetters = setters.get(actionClass);
		if (classSetters == null) {
			classSetters = new HashMap<String, Method>();
		}
		classSetters.put(ConversationUtil.buildKey(fieldName, fieldClass), method);
		setters.put(actionClass, classSetters);
	}
	
	public Map<String, Method> getSetterMethods(Class<?> clazz) {
		return setters.get(clazz);
	}
	
	public void addGetterMethod(Class<?> actionClass, Class<?> fieldClass, String fieldName, Method method) {
		Map<String, Method> classGetters = getters.get(actionClass);
		if (classGetters == null) {
			classGetters = new HashMap<String, Method>();
		}
		classGetters.put(ConversationUtil.buildKey(fieldName, fieldClass), method);
		getters.put(actionClass, classGetters);
	}
	
	public Map<String, Method> getGetterMethods(Class<?> clazz) {
		return getters.get(clazz);
	}
	*/
	
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
