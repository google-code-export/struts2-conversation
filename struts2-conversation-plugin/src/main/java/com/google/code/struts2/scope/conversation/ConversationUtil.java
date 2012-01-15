package com.google.code.struts2.scope.conversation;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.struts2.ServletActionContext;
import com.opensymphony.xwork2.ActionContext;

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
		for (String conversationName : conversations) {
			String id = getConversationId(conversationName);
			if (id != null) {
				Map<String, Object> conversationFieldMap = (Map<String, Object>) ActionContext.getContext().getSession().get(id);
				if (conversationFieldMap != null) {
					field = (T) conversationFieldMap.get(fieldName);
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
		for (String conversationName : getConversations()) {
			String convoId = getConversationId(conversationName);
			@SuppressWarnings("unchecked")
			Map<String, Object> conversationFieldMap = (Map<String, Object>) session.get(convoId);
			if (conversationFieldMap == null) {
				conversationFieldMap = new HashMap<String, Object>();
			}
			conversationFieldMap.put(fieldName, fieldValue);
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
			HttpServletRequest request = ServletActionContext.getRequest();
			if (request != null) {
				for (String paramName : request.getParameterMap().keySet()) {
					if (paramName.endsWith(ConversationConstants.CONVERSATION_NAME_SESSION_MAP_SUFFIX)) {
						convos.add(paramName);
					}
				}
			}
		}
		
		return convos.toArray(new String[convos.size()]);
	}
	
	/**
	 * Returns the given field's ConversationField name
	 * 
	 * @param field
	 * @return
	 */
	public static String getConversationFieldName(Field field) {
		String name = null;
		if (field.isAnnotationPresent(ConversationField.class)) {
			name = field.getAnnotation(ConversationField.class).name();
			if (name.equals(ConversationField.DEFAULT)) {
				name = field.getName();
			}
		} else {
			name = field.getName();
		}
		return name;
	}
	
}
