/***********************************************************************************************************************
 *
 * Struts2-Conversation-Plugin - An Open Source Conversation- and Flow-Scope Solution for Struts2-based Applications
 * =================================================================================================================
 *
 * Copyright (C) 2012 by Rees Byars
 * http://code.google.com/p/struts2-conversation/
 *
 ***********************************************************************************************************************
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 *
 ***********************************************************************************************************************
 *
 * $Id: HttpConversationUtil.java Apr 20, 2012 11:48:45 PM reesbyars $
 *
 **********************************************************************************************************************/
package com.google.code.rees.scope.conversation.context;

import javax.servlet.http.HttpSession;

import com.google.code.rees.scope.conversation.ConversationConstants;
import com.google.code.rees.scope.util.monitor.TimeoutMonitor;

/**
 * @author rees.byars
 *
 */
public class HttpConversationUtil {
	
	public static ConversationContextManager getContextManager(HttpSession session) {
		return (ConversationContextManager) session.getAttribute(ConversationConstants.CONVERSATION_CONTEXT_MANAGER_KEY);
	}
	
	public static void setContextManager(HttpSession session, ConversationContextManager contextManager) {
		session.setAttribute(ConversationConstants.CONVERSATION_CONTEXT_MANAGER_KEY, contextManager);
	}
	
	@SuppressWarnings("unchecked")
	public static TimeoutMonitor<ConversationContext> getTimeoutMonitor(HttpSession session) {
		return (TimeoutMonitor<ConversationContext>) session.getAttribute(ConversationConstants.CONVERSATION_TIMEOUT_MONITOR_KEY);
	}
	
	public static void setTimeoutMonitor(HttpSession session, TimeoutMonitor<ConversationContext> monitor) {
		session.setAttribute(ConversationConstants.CONVERSATION_TIMEOUT_MONITOR_KEY, monitor);
	}

}
