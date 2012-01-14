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

/**
 * 
 * A utility class that provides static methods that are used internally and
 * for unit testing.  Usage of this utility in a outside of these contexts is
 * discouraged.  Most of the methods are not optimized for other uses.
 * 
 * @author rees.byars
 *
 */
public class ConversationUtil {
	
	private static final Logger LOG = LoggerFactory
		.getLogger(ConversationUtil.class);
	
	/**
	 * Given the name of a conversation-scoped field and its class, this method
	 * returns the appropriate key that is used to identify instances of the 
	 * field in the conversation maps for the conversations of which it is a
	 * member.
	 * 
	 * @param name
	 * @param clazz
	 * @return
	 */
	public static String buildKey(String name, Class<?> clazz) {
		return clazz.getName() + "." + name;
	}
	
	/**
	 * Given a conversation name, returns the ID of the conversation for the currently
	 * executing thread.  
	 * 
	 * @param conversationName
	 * @return
	 */
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
	
	/**
	 * Given a conversation field's name and class, the value of the field is
	 * returned from a conversation map in the current thread.
	 * 
	 * @param <T>
	 * @param fieldName
	 * @param fieldClass
	 * @return
	 */
	public static <T> T getConversationField(String fieldName, Class<T> fieldClass) {
		return getConversationField(fieldName, fieldClass, getConversations());
	}
	
	/**
	 * Given a conversation field's name and class and an Array of conversation names, 
	 * the value of the field is returned from the first conversation in the Array
	 * that contains an instance of the field.
	 * 
	 * @param <T>
	 * @param fieldName
	 * @param fieldClass
	 * @param conversations
	 * @return
	 */
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
	
	/**
	 * Given a conversation field' name and an instance, the value is set
	 * for the field in all active conversations of which the field is a member.
	 * 
	 * @param fieldName
	 * @param fieldValue
	 */
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
	
	/**
	 * An array of all active conversations for the currently executing thread.
	 * 
	 * @return
	 */
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
	
	/**
	 * The current values of conversation fields annotated with {@link ConversationField} 
	 * are extracted from the target object and placed into the conversation maps
	 * for the currently executing thread. 
	 * 
	 * @param target
	 */
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
	
	/**
	 * The target object's Conversation fields that are annotated with 
	 * {@link ConversationField} are injected from any active conversations
	 * in the current thread of which the fields are members. 
	 * 
	 * @param target
	 */
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
	
	/**
	 * Returns the name of the given field's ConversationField
	 * 
	 * @param field
	 * @return
	 */
	protected static String getConversationFieldName(Field field) {
		String name = field.getAnnotation(ConversationField.class).name();
		if (name.equals(ConversationField.DEFAULT)) {
			name = field.getName();
		}
		return name;
	}
	
}
