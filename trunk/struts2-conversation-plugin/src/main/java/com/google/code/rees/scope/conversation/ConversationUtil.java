package com.google.code.rees.scope.conversation;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.code.rees.scope.conversation.annotations.ConversationField;

/**
 * 
 * A utility class that provides static methods that are used internally and
 * for unit testing.  Usage of this utility outside of these contexts is
 * discouraged.
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
	public static String getConversationId(String conversationName) {
		if (!conversationName.endsWith(ConversationConstants.CONVERSATION_NAME_SESSION_MAP_SUFFIX)) {
			conversationName += ConversationConstants.CONVERSATION_NAME_SESSION_MAP_SUFFIX;
		}
		ConversationAdapter adapter = ConversationAdapter.getAdapter();
		String id = (String) adapter.getRequestContext().get(conversationName);
		return id;
	}
	
	/**
	 * Given a conversation field's name, the value of the field is
	 * returned from a conversation map in the current thread.
	 * 
	 * @param fieldName
	 * @return
	 */
	public static Object getConversationField(String fieldName) {
		Object field = null;
		ConversationAdapter adapter = ConversationAdapter.getAdapter();
		if (adapter != null) {
			Map<String, Object> sessionContext = adapter.getSessionContext();
			Map<String, String> requestContext = adapter.getRequestContext();
			for (String conversationId : requestContext.values()) {
				@SuppressWarnings("unchecked")
				Map<String, Object> conversationContext = (Map<String, Object>) sessionContext.get(conversationId);
				if (conversationContext != null) {
					field = (Object) conversationContext.get(fieldName);
					if (field != null) break;
				}
			}
		}
		return field;
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
		ConversationAdapter adapter = ConversationAdapter.getAdapter();
		if (adapter != null) {
			Map<String, Object> session = adapter.getSessionContext();
			for (String conversationName : conversations) {
				String id = getConversationId(conversationName);
				if (id != null) {
					Map<String, Object> conversationContext = (Map<String, Object>) session.get(id);
					if (conversationContext != null) {
						field = (T) conversationContext.get(fieldName);
						if (field != null) break;
					}
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
		ConversationAdapter adapter = ConversationAdapter.getAdapter();
		if (adapter != null) {
			Map<String, Object> sessionContext = adapter.getSessionContext();
			Map<String, String> requestContext = adapter.getRequestContext();
			for (String conversationId : requestContext.values()) {
				@SuppressWarnings("unchecked")
				Map<String, Object> conversationContext = (Map<String, Object>) sessionContext.get(conversationId);
				if (conversationContext == null) {
					conversationContext = new HashMap<String, Object>();
				}
				conversationContext.put(fieldName, fieldValue);
				sessionContext.put(conversationId, conversationContext);
			}
		}
	}
	
	/**
	 * An array of all active conversations for the currently executing thread.
	 * 
	 * @return
	 */
	public static String[] getConversations() {
		
		List<String> convoIds = new ArrayList<String>();

		ConversationAdapter adapter = ConversationAdapter.getAdapter();
		if (adapter != null) {
			Map<String, String> requestContext = adapter.getRequestContext();
			convoIds.addAll(requestContext.keySet());
		}
		
		return convoIds.toArray(new String[convoIds.size()]);
	}
	
	/**
	 * Returns an array of all the conversation IDs on the current request
	 * @return
	 */
	public static String[] getConversationIds() {
		
		List<String> convoIds = new ArrayList<String>();

		ConversationAdapter adapter = ConversationAdapter.getAdapter();
		if (adapter != null) {
			Map<String, String> requestContext = adapter.getRequestContext();
			convoIds.addAll(requestContext.values());
		}
		
		return convoIds.toArray(new String[convoIds.size()]);
	}
	
	/**
	 * The conversations associated with a field
	 */
	public static String[] getConversations(Field field) {
		//TODO
		return null;
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
	
	public static String sanitizeConversationName(String conversationName) {
		return conversationName.replaceAll(":", "").replaceAll(",", "");
	}
	
}
