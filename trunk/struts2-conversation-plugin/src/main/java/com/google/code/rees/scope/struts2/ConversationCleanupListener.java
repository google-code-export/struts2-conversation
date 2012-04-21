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
 * $Id: ConversationCleanupListener.java Apr 20, 2012 11:32:39 PM reesbyars $
 *
 **********************************************************************************************************************/
package com.google.code.rees.scope.struts2;

import javax.servlet.annotation.WebListener;
import javax.servlet.http.HttpSessionEvent;
import javax.servlet.http.HttpSessionListener;

import com.google.code.rees.scope.conversation.context.ConversationContextManager;
import com.google.code.rees.scope.conversation.context.HttpConversationUtil;

/**
 * @author rees.byars
 */
@WebListener
public class ConversationCleanupListener implements HttpSessionListener {
    
	@Override
	public void sessionCreated(HttpSessionEvent se) {
		//do nothing - do not create the manager here as it may not be needed
	}

	@Override
	public void sessionDestroyed(HttpSessionEvent se) {
		ConversationContextManager contextManager = HttpConversationUtil.getContextManager(se.getSession());
		if (contextManager != null) {
			contextManager.destroy();
		}
	}

}
