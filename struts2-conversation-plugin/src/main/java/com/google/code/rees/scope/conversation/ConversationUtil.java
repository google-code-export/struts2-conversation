/*******************************************************************************
 * 
 *  Struts2-Conversation-Plugin - An Open Source Conversation- and Flow-Scope Solution for Struts2-based Applications
 *  =================================================================================================================
 * 
 *  Copyright (C) 2012 by Rees Byars
 *  http://code.google.com/p/struts2-conversation/
 * 
 * **********************************************************************************************************************
 * 
 *  Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with
 *  the License. You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 *  Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 *  an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 *  specific language governing permissions and limitations under the License.
 * 
 * **********************************************************************************************************************
 * 
 *  $Id: ConversationUtil.java reesbyars $
 ******************************************************************************/
package com.google.code.rees.scope.conversation;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.google.code.rees.scope.conversation.context.ConversationContext;

/**
 * Provides static methods intended primarily for use internally and in unit
 * testing. Use outside of these contexts should come only as a last resort.
 * 
 * @author rees.byars
 * 
 */
public class ConversationUtil {

	/**
	 * Given a conversation name, returns the ID of the conversation for the
	 * currently executing thread.
	 * 
	 * @param conversationName
	 * @return
	 */
	public static String getId(String conversationName) {
		if (!conversationName.endsWith(ConversationConstants.CONVERSATION_NAME_SUFFIX)) {
			conversationName += ConversationConstants.CONVERSATION_NAME_SUFFIX;
		}
		ConversationAdapter adapter = ConversationAdapter.getAdapter();
		String id = (String) adapter.getRequestContext().get(conversationName);
		return id;
	}

	/**
	 * Given a conversation field's name, the value of the field is returned
	 * from a conversation map in the current thread.
	 * 
	 * @param fieldName
	 * @return
	 */
	public static Object getField(String fieldName) {
		Object field = null;
		ConversationAdapter adapter = ConversationAdapter.getAdapter();
		if (adapter != null) {
			Map<String, String> requestContext = adapter.getRequestContext();
			for (Entry<String, String> entry : requestContext.entrySet()) {
				String name = entry.getKey();
				String id = entry.getValue();
				Map<String, Object> conversationContext = adapter.getConversationContext(name, id);
				if (conversationContext != null) {
					field = (Object) conversationContext.get(fieldName);
					if (field != null)
						break;
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
	public static <T> T getField(String fieldName, Class<T> fieldClass) {
		return getField(fieldName, fieldClass, getConversations());
	}

	/**
	 * Given a conversation field's name and class and an Array of conversation
	 * names, the value of the field is returned from the first conversation in
	 * the Array that contains an instance of the field.
	 * 
	 * @param <T>
	 * @param fieldName
	 * @param fieldClass
	 * @param conversations
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static <T> T getField(String fieldName, Class<T> fieldClass,String[] conversations) {
		T field = null;
		ConversationAdapter adapter = ConversationAdapter.getAdapter();
		if (adapter != null) {
			for (String conversationName : conversations) {
				String id = getId(conversationName);
				if (id != null) {
					Map<String, Object> conversationContext = adapter.getConversationContext(conversationName, id);
					if (conversationContext != null) {
						field = (T) conversationContext.get(fieldName);
						if (field != null)
							break;
					}
				}
			}
		}
		return field;
	}

	/**
	 * Given a conversation field's name and an instance, the value is set for
	 * the field in all active conversations of which the field is a member.
	 * 
	 * @param fieldName
	 * @param fieldValue
	 */
	public static void setField(String fieldName, Object fieldValue) {
		ConversationAdapter adapter = ConversationAdapter.getAdapter();
		if (adapter != null) {
			Map<String, String> requestContext = adapter.getRequestContext();
			for (Entry<String, String> entry : requestContext.entrySet()) {
				String name = entry.getKey();
				String id = entry.getValue();
				Map<String, Object> conversationContext = adapter.getConversationContext(name, id);
				if (conversationContext != null) {
					conversationContext.put(fieldName, fieldValue);
				}
			}
		}
	}
	
	/**
	 * A convenience method for beginning a conversation programmatically
	 * 
	 * @param name
	 * @param maxIdleTimeMillis
	 * @return
	 */
	public static ConversationContext begin(String name, long maxIdleTimeMillis, int maxInstances) {
		return begin(name, ConversationAdapter.getAdapter(), maxIdleTimeMillis, maxInstances);
	}
	
	/**
	 * A convenience method for beginning a conversation programmatically
	 * 
	 * @param name
	 * @param adapter
	 * @param maxIdleTimeMillis
	 * @return
	 */
	public static ConversationContext begin(String name, ConversationAdapter adapter, long maxIdleTimeMillis, int maxInstances) {
		ConversationContext context = adapter.beginConversation(name, maxIdleTimeMillis, maxInstances);
		String id = context.getId();
		adapter.getViewContext().put(name, id);
		adapter.getRequestContext().put(name, id);
		return context;
	}
	
	/**
	 * A convenience method for beginning a conversation programmatically
	 * 
	 * @param name
	 * @param maxIdleTimeMillis
	 * @return
	 */
	public static ConversationContext beginUsingSimpleName(String name, long maxIdleTimeMillis, int maxInstances) {
		return begin(name + ConversationConstants.CONVERSATION_NAME_SUFFIX, ConversationAdapter.getAdapter(), maxIdleTimeMillis, maxInstances);
	}

	/**
	 * Persist a conversation programmatically
	 * 
	 * @param name
	 * @return The {@link ConversationContext} or <code>null</code> if the
	 *         conversation is not active
	 */
	public static ConversationContext persist(String name) {
		return persist(name, ConversationAdapter.getAdapter());
	}

	/**
	 * Persist a conversation programmatically
	 * 
	 * @param name
	 * @return The {@link ConversationContext} or <code>null</code> if the
	 *         conversation is not active
	 */
	public static ConversationContext persist(String name, ConversationAdapter adapter) {
		String id = adapter.getRequestContext().get(name);
		if (id == null) {
			return null;
		}
		adapter.getViewContext().put(name, id);
		return adapter.getConversationContext(name, id);
	}
	
	/**
	 * Persist a conversation programmatically
	 * 
	 * @param name
	 * @return The {@link ConversationContext} or <code>null</code> if the
	 *         conversation is not active
	 */
	public static ConversationContext persistUsingSimpleName(String name) {
		return persist(name + ConversationConstants.CONVERSATION_NAME_SUFFIX, ConversationAdapter.getAdapter());
	}

	/**
	 * A convenience method for ending a conversation programmatically.
	 * 
	 * @param name
	 * @return The {@link ConversationContext} or <code>null</code> if the
	 *         conversation is not active
	 */
	public static ConversationContext end(String name) {
		return end(name, ConversationAdapter.getAdapter());
	}

	/**
	 * A convenience method for ending a conversation programmatically.
	 * 
	 * @param name
	 * @return The {@link ConversationContext} or <code>null</code> if the
	 *         conversation is not active
	 */
	public static ConversationContext end(String name, ConversationAdapter adapter) {
		String id = adapter.getRequestContext().remove(name);
		if (id == null) {
			return null;
		}
		adapter.getViewContext().remove(name);
		return adapter.endConversation(name, id);
	}
	
	/**
	 * A convenience method for ending a conversation programmatically.
	 * 
	 * @param name
	 * @return The {@link ConversationContext} or <code>null</code> if the
	 *         conversation is not active
	 */
	public static ConversationContext endUsingSimpleName(String name) {
		return end(name + ConversationConstants.CONVERSATION_NAME_SUFFIX, ConversationAdapter.getAdapter());
	}

	/**
	 * Given the conversation name, returns that conversation's context for the
	 * current request.
	 * 
	 * @param name
	 * @return The {@link ConversationContext} or <code>null</code> if the
	 *         conversation is not active
	 */
	public static ConversationContext getContext(String name) {
		ConversationAdapter adapter = ConversationAdapter.getAdapter();
		String id = adapter.getRequestContext().get(name);
		if (id == null) {
			return null;
		}
		return adapter.getConversationContext(name, id);
	}
	
	/**
	 * Given the conversation name without the "_conversation" suffix, returns that conversation's context for the
	 * current request.
	 * 
	 * @param name
	 * @return The {@link ConversationContext} or <code>null</code> if the
	 *         conversation is not active
	 */
	public static ConversationContext getContextUsingSimpleName(String name) {
		return getContext(name + ConversationConstants.CONVERSATION_NAME_SUFFIX);
	}

	/**
	 * An array of all active conversations for the current request.
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
	 * 
	 * @return
	 */
	public static String[] getIds() {

		List<String> convoIds = new ArrayList<String>();

		ConversationAdapter adapter = ConversationAdapter.getAdapter();
		if (adapter != null) {
			Map<String, String> requestContext = adapter.getRequestContext();
			convoIds.addAll(requestContext.values());
		}

		return convoIds.toArray(new String[convoIds.size()]);
	}

	/**
	 * Used to remove undesired characters from conversation names
	 * 
	 * @param conversationName
	 * @return
	 */
	public static String sanitizeName(String conversationName) {
		return conversationName.replaceAll(":", "").replaceAll(",", "");
	}

}
