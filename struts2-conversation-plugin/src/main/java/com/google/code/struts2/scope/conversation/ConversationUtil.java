package com.google.code.struts2.scope.conversation;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.struts2.ServletActionContext;

import com.google.code.struts2.scope.util.ReflectionUtil;
import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.util.logging.Logger;
import com.opensymphony.xwork2.util.logging.LoggerFactory;

public class ConversationUtil {
	
	private static final Logger LOG = LoggerFactory
		.getLogger(ConversationUtil.class);
	
	public static String buildKey(String name, Class<?> clazz) {
		return clazz.getName() + "." + name;
	}
	
	@SuppressWarnings("unchecked")
	public static String getConversationId(String conversationName) {
		if (!conversationName.endsWith(ConversationConstants.CONVERSATION_NAME_SESSION_MAP_SUFFIX)) {
			conversationName += ConversationConstants.CONVERSATION_NAME_SESSION_MAP_SUFFIX;
		}
		String id = (String) ServletActionContext.getRequest().getParameter(conversationName);
		if (id == null) {
			Map<String, String> convoIdMap = ((Map<String, String>) ActionContext.getContext().getValueStack()
					.findValue(ConversationConstants.CONVERSATION_ID_MAP_STACK_KEY));
			if (convoIdMap != null) {
				id =  convoIdMap.get(conversationName);
			}
		}
		return id;
	}
	
	public static <T> T getConversationField(String fieldName, Class<T> fieldClass) {
		return getConversationField(fieldName, fieldClass, getConversations());
	}
	
	@SuppressWarnings("unchecked")
	public static <T> T getConversationField(String fieldName, Class<T> fieldClass, String[] conversations) {
		T field = null;
		String key = buildKey(fieldName, fieldClass);
		for (String conversationName : conversations) {
			String id = getConversationId(conversationName);
			if (id != null) {
				Map<String, Object> conversationFieldMap = (Map<String, Object>) ActionContext.getContext().getSession().get(id);
				if (conversationFieldMap != null) {
					field = (T) conversationFieldMap.get(key);
					if (field != null) break;
				}
			}
		}
		return field;
	}
	
	public static void setConversationField(String fieldName, Object fieldValue) {
		Map<String, Object> session = ActionContext.getContext().getSession();
		String key = buildKey(fieldName, fieldValue.getClass());
		for (String conversationName : getConversations()) {
			String convoId = getConversationId(conversationName);
			@SuppressWarnings("unchecked")
			Map<String, Object> conversationFieldMap = (Map<String, Object>) session.get(convoId);
			if (conversationFieldMap == null) {
				conversationFieldMap = new HashMap<String, Object>();
			}
			conversationFieldMap.put(key, fieldValue);
			session.put(convoId, conversationFieldMap);
		}
	}
	
	@SuppressWarnings("unchecked")
	public static String[] getConversations() {
		
		List<String> convos = new ArrayList<String>();
		
		Map<String, String> convoMap = (Map<String, String>) ActionContext.getContext().getValueStack()
			.findValue(ConversationConstants.CONVERSATION_ID_MAP_STACK_KEY);
		
		if (convoMap != null) {
			convos.addAll(convoMap.keySet());
		} else {
			for (String paramName : ServletActionContext.getRequest().getParameterMap().keySet()) {
				if (paramName.endsWith(ConversationConstants.CONVERSATION_NAME_SESSION_MAP_SUFFIX)) {
					convos.add(paramName);
				}
			}
		}
		
		return convos.toArray(new String[convos.size()]);
	}
	
	public static void extractConversationFields(Object target) {
		Class<?> clazz = target.getClass();
		for (Field field : ReflectionUtil.getFields(clazz)) {
			if (field.isAnnotationPresent(ConversationField.class)) {
				String name = getConversationFieldName(field);
				ReflectionUtil.makeAccessible(field);
				try {
					Object value = field.get(target);
					if (value != null) {
						setConversationField(name, value);
					}
				} catch (IllegalArgumentException e) {
					LOG.info("Illegal Argument on conversation field " + field.getName());
				} catch (IllegalAccessException e) {
					LOG.info("Illegal Access on conversation field " + field.getName());
				}
			}
		}
	}
	
	public static void injectConversationFields(Object target) {
		for (Field field : ReflectionUtil.getFields(target.getClass())) {
			if (field.isAnnotationPresent(ConversationField.class)) {
				ConversationField cField = field.getAnnotation(ConversationField.class);
				String[] conversations = cField.conversations();
				Object value;
				if (conversations.length == 0) {
					value = getConversationField(field.getName(), field.getType());
				} else {
					value = getConversationField(field.getName(), field.getType(), conversations);
				} 
				ReflectionUtil.makeAccessible(field);
				try {
					field.set(target, value);
				} catch (IllegalArgumentException e) {
					LOG.info("Illegal Argument on conversation field " + field.getName());
				} catch (IllegalAccessException e) {
					LOG.info("Illegal Access on conversation field " + field.getName());
				}
			}
		}
	}
	
	protected static String getConversationFieldName(Field field) {
		String name = field.getAnnotation(ConversationField.class).name();
		if (name.equals(ConversationField.DEFAULT)) {
			name = field.getName();
		}
		return name;
	}
	
}
