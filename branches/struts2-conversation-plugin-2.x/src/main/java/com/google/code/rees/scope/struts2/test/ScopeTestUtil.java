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
 *  $Id: ScopeTestUtil.java reesbyars $
 ******************************************************************************/
package com.google.code.rees.scope.struts2.test;

import java.util.Map;
import java.util.Map.Entry;

import org.apache.struts2.dispatcher.Dispatcher;
import org.springframework.mock.web.MockHttpServletRequest;

import com.google.code.rees.scope.conversation.ConversationAdapter;
import com.google.code.rees.scope.conversation.ConversationConstants;
import com.google.code.rees.scope.conversation.ConversationUtil;
import com.google.code.rees.scope.conversation.annotations.ConversationField;
import com.google.code.rees.scope.conversation.configuration.DefaultConversationArbitrator;
import com.google.code.rees.scope.conversation.processing.ConversationProcessor;
import com.google.code.rees.scope.conversation.processing.DefaultInjectionConversationProcessor;
import com.google.code.rees.scope.session.SessionField;
import com.google.code.rees.scope.session.SessionUtil;
import com.google.code.rees.scope.struts2.ConventionConstants;
import com.google.code.rees.scope.struts2.StrutsScopeConstants;
import com.opensymphony.xwork2.ActionContext;

/**
 * A utility for simplifying Struts2 unit testing against the interceptor stack
 * 
 * @author rees.byars
 */
public class ScopeTestUtil {

	private static DefaultInjectionConversationProcessor manager;
	private static DefaultConversationArbitrator arbitrator = new DefaultConversationArbitrator();
	private static String actionSuffix;

	protected static DefaultInjectionConversationProcessor getconversationManager() {
		if (manager == null) {
			manager = (DefaultInjectionConversationProcessor) Dispatcher.getInstance().getContainer().getInstance(ConversationProcessor.class, StrutsScopeConstants.CONVERSATION_PROCESSOR_KEY);
		}
		return manager;
	}

	protected static String getActionSuffix() {
		if (actionSuffix == null) {
			actionSuffix = Dispatcher.getInstance().getContainer().getInstance(String.class, ConventionConstants.ACTION_SUFFIX);
		}
		return actionSuffix;
	}

	/**
	 * For unit testing, sets the conversation IDs of the conversations in the
	 * current thread onto a given mock request.
	 * 
	 * @param request
	 */
	public static void setConversationIdsOnRequest(MockHttpServletRequest request, Class<?> actionClass) {
		ActionContext actionContext = ActionContext.getContext();
		@SuppressWarnings("unchecked")
		Map<String, String> convoIdMap = ((Map<String, String>) actionContext.getValueStack().findValue(StrutsScopeConstants.CONVERSATION_ID_MAP_STACK_KEY));
		if (convoIdMap != null) {
			for (Entry<String, String> entry : convoIdMap.entrySet()) {
				request.addParameter(entry.getKey(), new String[] { entry.getValue() });
			}
		} else {
			for (String conversationName : arbitrator.getConversations(actionClass, getActionSuffix())) {
				request.addParameter(ConversationUtil.sanitizeName(conversationName) + ConversationConstants.CONVERSATION_NAME_SUFFIX, conversationName + "-test-id");
			}
		}
	}

	/**
	 * The current values of session and conversation fields that are annotated
	 * with {@link SessionField} and {@link ConversationField} are extracted
	 * from the target object and placed into the session and the active
	 * conversations available in the current thread.
	 * 
	 * @param target
	 */
	public static void extractScopeFields(Object target) {
		SessionUtil.extractFields(target);
		getconversationManager().extractConversationFields(target, ConversationAdapter.getAdapter());
	}

	/**
	 * The target object's session and conversation fields that are annotated
	 * with {@link SessionField} and {@link ConversationField} are injected from
	 * the session and the active conversations available in the current thread.
	 * 
	 * @param target
	 */
	public static void injectScopeFields(Object target) {
		SessionUtil.injectFields(target);
		getconversationManager().injectConversationFields(target, ConversationAdapter.getAdapter());
	}

}
